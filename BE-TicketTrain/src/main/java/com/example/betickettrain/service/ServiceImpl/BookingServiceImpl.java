package com.example.betickettrain.service.ServiceImpl;

import com.example.betickettrain.anotation.SystemLogAspect;
import com.example.betickettrain.dto.*;
import com.example.betickettrain.entity.*;
import com.example.betickettrain.exceptions.BusinessException;
import com.example.betickettrain.exceptions.ErrorCode;
import com.example.betickettrain.exceptions.SeatLockedException;
import com.example.betickettrain.mapper.BookingMapper;
import com.example.betickettrain.mapper.PaymentMapper;
import com.example.betickettrain.mapper.TicketMapper;
import com.example.betickettrain.mapper.TripMapper;
import com.example.betickettrain.repository.*;
import com.example.betickettrain.service.*;
import com.example.betickettrain.util.Constants;
import com.example.betickettrain.util.DateUtils;
import com.example.betickettrain.util.TemplateMail;
import com.example.betickettrain.util.utils;
import jakarta.mail.MessagingException;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.image.BufferedImage;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static com.example.betickettrain.util.Constants.qrBaseUrl;
import static com.example.betickettrain.util.QrCodeGenerator.bufferedImageToByteArray;
import static com.example.betickettrain.util.QrCodeGenerator.generateQRCodeImage;

@Service
@AllArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final TicketMapper ticketMapper;
    private final PaymentMapper paymentMapper;
    private final RedisSeatLockService redisSeatLockService;
    private final BookingRepository bookingRepository;
    private final TripRepository tripRepository;
    private final SeatRepository seatRepository;
    private final TicketRepository ticketRepository;
    private final VnpayService vnpayService;
    private final TicketPriceRepository ticketPriceRepository;
    private final PromotionRepository promotionRepository;
    private final BookingPromotionRepository bookingPromotionRepository;
    private final EmailService emailService;
    private final UserRepository userRepository;
    private final BookingMapper bookingMapper;

    private final NotificationRepository notificationRepository;
    private final PaymentRepository paymentRepository;
    private final TripMapper tripMapper;
    private final NotificationService notificationService;
    private final SystemLogRepository systemLogRepository;
    private final SystemLogService systemLogService;
    // chưa hoàn thiện vẫn chưa fix dc race condition
    @Override
    public void lockSeats(BookingLockRequest request) {
        // Lấy danh sách các ghế cần  lock cho chuyến này
        //doan nay chưa clean (*)
        Set<Integer> lockedSeats = redisSeatLockService.getLockedSeats(request.getTripId());
        List<Integer> seatIsLock = new ArrayList<>();

        // Kiểm tra xem có ghế nào trong request đã bị lock chưa
        for (Integer seatId : request.getSeatIds()) {
            if (lockedSeats.contains(seatId)) {
                seatIsLock.add(seatId);
            }
        }
        // Nếu có ghế bị khóa, ném ngoại lệ với danh sách ghế bị khóa
        if (!seatIsLock.isEmpty()) {
            String errorMessage = String.format("Các ghế sau đã bị khóa: %s", seatIsLock);
            log.warn("Không thể khóa ghế: {}", errorMessage);
            throw new SeatLockedException(ErrorCode.SEAT_LOCK, errorMessage, seatIsLock);
        }
        // Nếu tất cả ghế đều chưa bị lock, tiến hành lock
        for (Integer seatId : request.getSeatIds()) {
            boolean locked = redisSeatLockService.tryLockSeat(request.getTripId(), seatId, Duration.ofMinutes(15));
            if (!locked) {
                throw new SeatLockedException(ErrorCode.SEAT_LOCK, "ghế đã bị khóa : " + seatId + ".");
            }
            log.info("✅ Locked seat: {}", seatId);
        }
    }

    //    @Override
//    public void lockSeats(BookingLockRequest request) {
//        String script = "local result = 1 " +
//                "for i, seatId in ipairs(ARGV) do " +
//                "  if redis.call('SETNX', KEYS[1]..':'..seatId, 'locked') == 0 then " +
//                "    result = 0 " +
//                "    break " +
//                "  else " +
//                "    redis.call('EXPIRE', KEYS[1]..':'..seatId, ARGV[1]) " +
//                "  end " +
//                "end " +
//                "if result == 0 then " +
//                "  for i, seatId in ipairs(ARGV) do " +
//                "    if redis.call('GET', KEYS[1]..':'..seatId) == 'locked' then " +
//                "      redis.call('DEL', KEYS[1]..':'..seatId) " +
//                "    end " +
//                "  end " +
//                "end " +
//                "return result";
//
//        List<String> keys = Collections.singletonList(request.getTripId().toString());
//        List<String> args = new ArrayList<>();
//        args.add(String.valueOf(Duration.ofMinutes(8).getSeconds())); // TTL
//        args.addAll(request.getSeatIds().stream().map(Object::toString).collect(Collectors.toList()));
//
//        Long result = (Long) redisTemplate.execute(new DefaultRedisScript<>(script, Long.class), keys, args);
//
//        if (result == 0) {
//            throw new SeatLockException("One or more seats are already locked: " + request.getSeatIds());
//        }
//
//        for (Integer seatId : request.getSeatIds()) {
//            log.info("✅ Locked seat: {}", seatId);
//        }
//    }
    @Override
    @Transactional
    public String initiateCheckout(BookingCheckoutRequest request, User user) {
        try {
            // ======= Bước 1: Lock ghế lượt đi =======
            List<Integer> outboundSeatIds = request.getPassengerTickets().stream().map(PassengerTicketDto::getSeatId).toList();
            lockSeats(new BookingLockRequest(request.getTripId(), outboundSeatIds));

            // Nếu có chiều về thì lock luôn
            if (request.getReturnTripId() != null && request.getReturnPassengerTickets() != null) {
                List<Integer> returnSeatIds = request.getReturnPassengerTickets().stream().map(PassengerTicketDto::getSeatId).toList();
                lockSeats(new BookingLockRequest(request.getReturnTripId(), returnSeatIds));
            }

            // ======= Bước 2: Lấy thông tin user và tạo Booking =======
            //  User userInDb = userRepository.findById(user.getUserId());
            Booking booking = new Booking();
            booking.setUser(user);
            booking.setContactEmail(request.getInfoEmail());
            booking.setContactPhone(request.getInfoPhone());
            booking.setBookingCode("BK" + DateUtils.toString(LocalDateTime.now()));
            booking.setBookingStatus(Booking.BookingStatus.pending);
            booking.setPaymentMethod(Booking.PaymentMethod.valueOf(request.getPaymentMethod().toLowerCase()));
            booking.setPaymentStatus(Booking.PaymentStatus.pending);
            booking.setCreatedAt(LocalDateTime.now());
            booking.setTotalAmount(0.0);
            booking = bookingRepository.save(booking);

            // ======= Bước 3: Xử lý cả lượt đi & về =======
            double totalBeforePromotion = 0.0;

            totalBeforePromotion += createTicketsForTrip(request.getTripId(), request.getPassengerTickets(), booking, false);

            if (request.getReturnTripId() != null && request.getReturnPassengerTickets() != null) {
                totalBeforePromotion += createTicketsForTrip(request.getReturnTripId(), request.getReturnPassengerTickets(), booking, true);
            }

            double totalAfterPromotion = totalBeforePromotion;

                // Áp dụng mã giảm giá nếu có
            if (request.getPromotionCode() != null && !request.getPromotionCode().isBlank()) {
                totalAfterPromotion = applyPromotion(booking, request.getPromotionCode(), totalBeforePromotion);
            }

            booking.setTotalAmount(totalAfterPromotion);
            bookingRepository.save(booking);


            // ======= Bước 5: Gửi link thanh toán =======
            return "VNPAY".equalsIgnoreCase(request.getPaymentMethod()) ? vnpayService.generatePaymentUrl(booking) : "/payment-success-local?bookingCode=" + booking.getBookingCode();

        } catch (Exception e) {
            // ======= Giải phóng toàn bộ ghế đã lock nếu lỗi =======
            List<Integer> outboundSeatIds = request.getPassengerTickets().stream().map(PassengerTicketDto::getSeatId).toList();
            for (Integer seatId : outboundSeatIds) {
                redisSeatLockService.unlockSeat(request.getTripId(), seatId);
            }

            if (request.getReturnTripId() != null && request.getReturnPassengerTickets() != null) {
                List<Integer> returnSeatIds = request.getReturnPassengerTickets().stream().map(PassengerTicketDto::getSeatId).toList();
                for (Integer seatId : returnSeatIds) {
                    redisSeatLockService.unlockSeat(request.getReturnTripId(), seatId);
                }
            }
            throw e;
        }
    }

    // tạo ticket cchung cho di và về
    private double createTicketsForTrip(Integer tripId, List<PassengerTicketDto> ticketsDto, Booking booking, boolean isReturn) {
        Trip trip = tripRepository.findById(tripId).orElseThrow(() -> new RuntimeException("Trip not found: " + tripId));
        Route route = trip.getRoute();
        if (route == null) throw new RuntimeException("Route not found for trip");

        Map<Integer, Seat> seatMap = seatRepository.findBySeatIdIn(ticketsDto.stream().map(PassengerTicketDto::getSeatId).toList()).stream().collect(Collectors.toMap(Seat::getSeatId, s -> s));

        List<Carriage.CarriageType> carriageTypes = seatMap.values().stream().map(seat -> seat.getCarriage().getCarriageType()).distinct().toList();

        Map<Carriage.CarriageType, TicketPrice> priceMap = ticketPriceRepository.findByRouteAndCarriageTypeAndDateRange(route.getRouteId(), carriageTypes, trip.getDepartureTime().toLocalDate()).stream().collect(Collectors.toMap(TicketPrice::getCarriageType, tp -> tp));

        double total = 0.0;

        for (PassengerTicketDto pt : ticketsDto) {
            Seat seat = seatMap.get(pt.getSeatId());
            if (seat == null) throw new RuntimeException("Seat not found: " + pt.getSeatId());

            TicketPrice price = priceMap.get(seat.getCarriage().getCarriageType());
            if (price == null)
                throw new RuntimeException("No price for carriage type: " + seat.getCarriage().getCarriageType());

            double ticketPrice = calculateDynamicPrice(price, trip.getDepartureTime());
            String ticketCode = generateTicketCode();

            Ticket ticket = Ticket.builder().booking(booking).trip(trip).seat(seat).originStation(route.getOriginStation()).destinationStation(route.getDestinationStation()).passengerName(pt.getPassengerName()).passengerIdCard(pt.getIdentityCard()).ticketPrice(ticketPrice).ticketCode(ticketCode).status(Ticket.Status.hold).holdExpireTime(LocalDateTime.now().plusMinutes(15)).createdAt(LocalDateTime.now())
                    //  .isReturnTrip(isReturn) // bạn cần thêm field này nếu phân biệt lượt về
                    .build();

           ticketRepository.save(ticket);
            total += ticketPrice;
        }

        return total;
    }


    // chưa tối ưu vì nêu cập nhật db fail thì exception dù user đã thanh toán . Để sau update thêm (*)
    @Transactional
    @Override
    public boolean handleVnPayCallback(String bookingCode, String responseCode) {
        try {
            // Tìm booking theo booking code
            Booking booking = bookingRepository.findByBookingCode(bookingCode).orElseThrow(() -> new RuntimeException("Booking not found with code: " + bookingCode));

            // Kiểm tra booking đã được xử lý chưa
            if (booking.getPaymentStatus() != Booking.PaymentStatus.pending) {
                log.warn("Booking {} already processed with status: {}", bookingCode, booking.getPaymentStatus());
                return booking.getPaymentStatus() == Booking.PaymentStatus.paid;
            }

            // Xử lý response từ VnPay
            if ("00".equals(responseCode)) {
                // Thanh toán thành công
                return handleSuccessfulPayment(booking);
            } else {
                // Thanh toán thất bại
                return handleFailedPayment(booking, responseCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error handling VnPay callback for booking: {}", bookingCode, e);
            return false;
        }
    }

    @Override
    public Page<BookingDto> findBookings(String search, String bookingStatus, Pageable pageable) {
        Specification<Booking> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Tìm theo mã booking
            if (search != null && !search.isBlank()) {
                String pattern = "%" + search.toLowerCase() + "%";
                predicates.add(cb.like(cb.lower(root.get("bookingCode")), pattern));
            }

            // Tìm theo trạng thái booking
            if (bookingStatus != null && !bookingStatus.equalsIgnoreCase("all")) {
                try {
                    Booking.BookingStatus statusEnum = Booking.BookingStatus.valueOf(bookingStatus.toLowerCase());
                    predicates.add(cb.equal(root.get("bookingStatus"), statusEnum));
                } catch (IllegalArgumentException ignored) {
                    // Nếu không hợp lệ thì bỏ qua
                }
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return bookingRepository.findAll(spec, pageable).map(booking -> {
            BookingDto dto = bookingMapper.toDto(booking);

            List<Ticket> tickets = ticketRepository.findByBookingBookingId(booking.getBookingId());

            dto.setTicketCount(tickets.size());

            // Lấy danh sách passenger theo vé
            List<PassengerTicketDto> passengerTicketDtos = new ArrayList<>();
            TripDto tripDto = tripMapper.toDto(tickets.get(0).getTrip());

            for (Ticket ticket : tickets) {
                PassengerTicketDto passengerTicketDto = new PassengerTicketDto();
                passengerTicketDto.setSeatId(ticket.getSeat().getSeatId());
                passengerTicketDto.setPassengerName(ticket.getPassengerName());
                passengerTicketDto.setIdentityCard(ticket.getPassengerIdCard());
                passengerTicketDto.setSeatNumbers(ticket.getSeat().getSeatNumber());
                passengerTicketDto.setCarriageNumber(ticket.getSeat().getCarriage().getCarriageNumber());
                passengerTicketDto.setTrainName(ticket.getTrip().getTrain().getTrainName());

                passengerTicketDtos.add(passengerTicketDto);
            }

            dto.setTripDto(tripDto);
            dto.setPassengerTicketDtos(passengerTicketDtos);
            return dto;
        });
    }


    //chua toi uu
    @Override
    public Page<BookingHistoryDTO> getBookingHistorybyUser(Long userId, Pageable pageable) {
        Page<Booking> bookings = bookingRepository.findAllByUser_UserIdAndPaymentStatusIn(
                userId,
                List.of(
                        Booking.PaymentStatus.paid,
                        Booking.PaymentStatus.refunded,
                        Booking.PaymentStatus.cancelled
                ),
                pageable
        );

        Executor executor = Executors.newFixedThreadPool(10);

        List<BookingHistoryDTO> bookingHistoryDTOs = bookings
                .stream()
                .map(booking -> {
                    List<Ticket> tickets = ticketRepository.findByBookingBookingId(booking.getBookingId());
                    List<TicketDto> ticketDtos = tickets.stream()
                            .map(ticketMapper::toDto)
                            .toList();

                    PaymentDto payment = paymentMapper.toDto(
                            paymentRepository.findByBooking_BookingId(booking.getBookingId())
                    );

                    List<PassengerTicketDto> passengerTicketDtos = ticketDtos.stream()
                            .map(t -> new PassengerTicketDto(
                                    t.getSeat().getSeatId(),
                                    t.getPassengerName(),
                                    t.getPassengerIdCard(),
                                    t.getSeat().getSeatNumber(),
                                    t.getSeat().getCarriage().getCarriageNumber(),
                                    t.getTrip().getTrain().getTrainName()
                            ))
                            .toList();

                    BookingHistoryDTO dto = new BookingHistoryDTO();
                    dto.setBookingId(booking.getBookingId());
                    dto.setBookingCode(booking.getBookingCode());
                    dto.setBookingStatus(booking.getBookingStatus().toString());
                    dto.setTotalAmount(booking.getTotalAmount());
                    dto.setCreatedAt(booking.getCreatedAt());
                    dto.setPayment(payment);
                    dto.setTrip(!ticketDtos.isEmpty() ? ticketDtos.get(0).getTrip() : null);
                    dto.setPassengers(passengerTicketDtos);

                    return dto;
                })
                .map(dto -> CompletableFuture.supplyAsync(() -> dto, executor))
                .map(CompletableFuture::join)
                .toList();

        return new PageImpl<>(bookingHistoryDTOs, pageable, bookings.getTotalElements());
    }


    @Override
    public BookingDto findBookingByBookingCode(String bookingCode) {
        return bookingRepository.findByBookingCode(bookingCode).map(bookingMapper::toDto).orElseThrow(() -> new ResourceNotFoundException("Booking not found with code: " + bookingCode));
    }

    @Override
    public void markTicketsCheckedIn(Integer bookingId) {
        List<Ticket> tickets = ticketRepository.findAllByBookingBookingId(bookingId);
        if (tickets == null || tickets.isEmpty()) {
            throw new ResourceNotFoundException("No tickets found for booking ID: " + bookingId);
        }
        for (Ticket ticket : tickets) {
            // Chỉ check-in nếu vé đang ở trạng thái 'booked'
            if (Ticket.Status.booked.equals(ticket.getStatus())) {
                ticket.setStatus(Ticket.Status.checked_in);
            }
        }

        ticketRepository.saveAll(tickets); // cập nhật hàng loạt
    }


    private double calculateDynamicPrice(TicketPrice price, LocalDateTime depTime) {
        double total = price.getBasePrice();
        if (isWeekend(depTime.toLocalDate())) total += Optional.ofNullable(price.getWeekendSurcharge()).orElse(0.0);
        if (isHoliday(depTime.toLocalDate())) total += Optional.ofNullable(price.getHolidaySurcharge()).orElse(0.0);
        if (isPeakHour(depTime.toLocalTime())) total += Optional.ofNullable(price.getPeakHourSurcharge()).orElse(0.0);
        if (price.getDiscountRate() != null && price.getDiscountRate() > 0) {
            total *= (1 - price.getDiscountRate() / 100.0);
        }
        return total;
    }

    /**
     * Kiểm tra có phải cuối tuần không
     */
    private boolean isWeekend(LocalDate date) {
        return date.getDayOfWeek().getValue() >= 6; // Saturday = 6, Sunday = 7
    }

    /**
     * Kiểm tra có phải ngày lễ không (cần implement logic cụ thể)
     */
    private boolean isHoliday(LocalDate date) {
        // TODO: Implement holiday checking logic
        // Có thể tạo bảng holidays hoặc hard-code các ngày lễ
        return false;
    }

    /**
     * Kiểm tra có phải giờ cao điểm không
     */
    private boolean isPeakHour(LocalTime time) {
        // Giờ cao điểm: 6-9h sáng và 17-20h chiều
        return (time.isAfter(LocalTime.of(6, 0)) && time.isBefore(LocalTime.of(9, 0))) || (time.isAfter(LocalTime.of(17, 0)) && time.isBefore(LocalTime.of(20, 0)));
    }

    /**
     * Tạo ticket code unique
     */
    private String generateTicketCode() {
        return "TK" + System.currentTimeMillis() + String.format("%03d", (int) (Math.random() * 1000));
    }

    /**
     * Áp dụng promotion và lưu vào booking_promotion
     */
    private double applyPromotion(Booking booking, String promotionCode, double originalAmount) {
        // Tìm promotion hợp lệ theo code và status active
        Promotion promotion = promotionRepository.findByPromotionCodeAndStatus(promotionCode, Promotion.Status.active).orElseThrow(() -> new RuntimeException("Invalid or inactive promotion code"));

        // Kiểm tra thời hạn
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(promotion.getStartDate()) || now.isAfter(promotion.getEndDate())) {
            throw new RuntimeException("Promotion code has expired");
        }

        // Kiểm tra số lượng sử dụng
        if (promotion.getUsageLimit() != null) {
            int currentUsageCount = promotion.getUsageCount() != null ? promotion.getUsageCount() : 0;
            if (currentUsageCount >= promotion.getUsageLimit()) {
                throw new RuntimeException("Promotion usage limit exceeded");
            }
        }

        // Kiểm tra điều kiện mua tối thiểu
        if (promotion.getMinimumPurchase() != null && originalAmount < promotion.getMinimumPurchase()) {
            throw new RuntimeException("Order amount does not meet minimum purchase requirement: " + promotion.getMinimumPurchase());
        }

        // Tính discount
        double discountAmount = 0;
        if (promotion.getDiscountType() == Promotion.DiscountType.percentage) {
            discountAmount = originalAmount * (promotion.getDiscountValue() / 100);
            // Áp dụng maximum discount nếu có
            if (promotion.getMaximumDiscount() != null) {
                discountAmount = Math.min(discountAmount, promotion.getMaximumDiscount());
            }
        } else if (promotion.getDiscountType() == Promotion.DiscountType.fixed_amount) {
            discountAmount = promotion.getDiscountValue();
            // Đảm bảo discount không vượt quá tổng tiền
            discountAmount = Math.min(discountAmount, originalAmount);
        }

        double finalAmount = Math.max(0, originalAmount - discountAmount);

        // Lưu vào booking_promotion
        BookingPromotion bookingPromotion = BookingPromotion.builder().booking(booking).promotion(promotion).discountAmount(discountAmount).appliedAt(LocalDateTime.now()).build();
        bookingPromotionRepository.save(bookingPromotion);

        // Cập nhật usage count của promotion
        int newUsageCount = (promotion.getUsageCount() != null ? promotion.getUsageCount() : 0) + 1;
        promotion.setUsageCount(newUsageCount);
        promotionRepository.save(promotion);

        return finalAmount;
    }

    /**
     * Xử lý thanh toán thành công
     */
    @Transactional()
    public boolean handleSuccessfulPayment(Booking booking) {
        try {
            // Cập nhật trạng thái booking
            booking.setPaymentStatus(Booking.PaymentStatus.paid);
            booking.setBookingStatus(Booking.BookingStatus.confirmed);
            booking.setPaymentDate(LocalDateTime.now());
            bookingRepository.save(booking);

            // Cập nhật trạng thái tickets
            List<Ticket> tickets = ticketRepository.findByBookingBookingId(booking.getBookingId());
            for (Ticket ticket : tickets) {
                ticket.setStatus(Ticket.Status.booked);
                // Xóa hold expire time vì đã thanh toán
                ticket.setHoldExpireTime(null);
                systemLogService.logAction(SystemLog.builder()
                        .user(booking.getUser())
                        .action("Xác nhận vé")
                        .entityType("Ticket")
                        .entityId(ticket.getTicketId())
                        .description("Vé " + ticket.getSeat().getSeatNumber() +
                                " đã được xác nhận cho chuyến " + ticket.getTrip().getTripCode() +
                                ", mã đặt vé: " + booking.getBookingCode())
                        .build());
            }
            ticketRepository.saveAll(tickets);

            Payment payment = Payment.builder()
                    .booking(booking)
                    .paymentAmount(booking.getTotalAmount())
                    .paymentMethod(booking.getPaymentMethod())
                    .transactionId("VNPAY_" + booking.getBookingCode())
                    .status(Payment.Status.completed)
                    .paymentDetails("{\"gateway\": \"VNPAY\", \"responseCode\": \"00\"}")
                    .build();

            paymentRepository.save(payment);
            // Giải phóng lock ghế (vì đã book thành công)
            for (Ticket ticket : tickets) {
                redisSeatLockService.unlockSeat(ticket.getTrip().getTripId(), ticket.getSeat().getSeatId());
            }
            // Gửi email xác nhận (nếu có)
            try {

                sendBookingConfirmationEmail(booking);
            } catch (Exception e) {
                e.printStackTrace();
                log.warn("Failed to send confirmation email for booking: {}", booking.getBookingCode(), e);
                // Không throw exception vì thanh toán đã thành công
            }
            systemLogService.logAction(SystemLog.builder()
                    .user(booking.getUser()) // giả sử booking có user
                    .action("Thanh toán thành công")
                    .entityType("Booking")
                    .entityId(booking.getBookingId())
                    .description("Thanh toán thành công cho mã đặt vé: " + booking.getBookingCode())
                    .ipAddress(null) // có thể set nếu biết
                    .userAgent(null) // có thể set nếu biết
                    .build());
            log.debug("📘 Sent log to WebSocket");
            log.info("Payment successful for booking: {}", booking.getBookingCode());
            return true;

        } catch (Exception e) {

            log.error("Error processing successful payment for booking: {}", booking.getBookingCode(), e);
            throw e;
        }
    }

    /**
     * Xử lý thanh toán thất bại
     */
    public boolean handleFailedPayment(Booking booking, String responseCode) {
        try {
            // Cập nhật trạng thái booking
            booking.setPaymentStatus(Booking.PaymentStatus.cancelled);
            booking.setBookingStatus(Booking.BookingStatus.cancelled);
            bookingRepository.save(booking);

            // Cập nhật trạng thái tickets
            List<Ticket> tickets = ticketRepository.findByBookingBookingId(booking.getBookingId());
            for (Ticket ticket : tickets) {
                ticket.setStatus(Ticket.Status.cancelled);
            }
            ticketRepository.saveAll(tickets);

            // Giải phóng lock ghế
            for (Ticket ticket : tickets) {
                redisSeatLockService.unlockSeat(ticket.getTrip().getTripId(), ticket.getSeat().getSeatId());
            }

            // Hoàn lại usage count của promotion nếu có
            rollbackPromotionUsage(booking);

            // ✅ Lưu bản ghi Payment thất bại
            Payment payment = Payment.builder().booking(booking).paymentAmount(booking.getTotalAmount()).paymentMethod(booking.getPaymentMethod()).transactionId("VNPAY_" + booking.getBookingCode()).status(Payment.Status.failed).paymentDetails("{\"gateway\": \"VNPAY\", \"responseCode\": \"" + responseCode + "\"}").build();

            paymentRepository.save(payment);

            log.info("Payment failed for booking: {} with response code: {}", booking.getBookingCode(), responseCode);
            return false;

        } catch (Exception e) {
            log.error("Error processing failed payment for booking: {}", booking.getBookingCode(), e);
            throw e;
        }
    }

    /**
     * Hoàn lại usage count của promotion
     */
    public void rollbackPromotionUsage(Booking booking) {
        try {
            List<BookingPromotion> bookingPromotions = bookingPromotionRepository.findAllByBookingBookingId(booking.getBookingId());

            for (BookingPromotion bp : bookingPromotions) {
                Promotion promotion = bp.getPromotion();
                if (promotion.getUsageCount() != null && promotion.getUsageCount() > 0) {
                    promotion.setUsageCount(promotion.getUsageCount() - 1);
                    promotionRepository.save(promotion);
                }
            }
        } catch (Exception e) {
            log.warn("Failed to rollback promotion usage for booking: {}", booking.getBookingCode(), e);
        }
    }

    @Async
    public void scheduleEmailRetry(BookingDto booking, int retryCount) {
        if (retryCount > 3) {
            log.error("All email retries failed for booking: {}", booking.getBookingCode());
            emailService.createPermanentFailureNotification(booking);
            return;
        }

        try {
            // Delay tăng dần: 1 phút, 5 phút, 15 phút
            int delayMinutes = retryCount == 1 ? 1 : retryCount == 2 ? 5 : 15;
            Thread.sleep(delayMinutes * 60 * 1000);

            sendBookingConfirmationEmail(bookingMapper.toEntity(booking));
            log.info("Email retry {} successful for booking: {}", retryCount, booking.getBookingCode());

            // Update notification về việc gửi email thành công
            updateNotificationWithEmailSuccess(booking);

        } catch (Exception e) {
            log.warn("Email retry {} failed for booking: {}", retryCount, booking.getBookingCode());
            scheduleEmailRetry(booking, retryCount + 1);
        }
    }

    // Update notification khi email cuối cùng thành công
    public void updateNotificationWithEmailSuccess(BookingDto booking) {
        try {
            // Tìm notification của booking này
            List<Notification> notifications = notificationRepository.findByUserUserIdAndRelatedIdAndNotificationType(booking.getUser().getUserId(), booking.getBookingId(), Notification.NotificationType.booking);

            if (!notifications.isEmpty()) {
                Notification notification = notifications.get(0);
                notification.setMessage(notification.getMessage() + "\n\nEmail xác nhận đã được gửi thành công!");
                notificationRepository.save(notification);
            }
        } catch (Exception e) {
            log.warn("Failed to update notification with email success", e);
        }
    }

    /**
     * Gửi email xác nhận booking
     */
    public void sendBookingConfirmationEmail(Booking booking) throws MessagingException {

        // Truy xuất đầy đủ dữ liệu cần trước khi vào thread mới
        List<TicketDto> tickets = ticketRepository.findByBookingBookingId(booking.getBookingId()).stream().map(ticketMapper::toDto).collect(Collectors.toList());

        BookingDto bookingDto = bookingMapper.toDto(booking);
        String bookingCode = booking.getBookingCode(); // sẵn booking code
//        String userEmail = booking.getUser().getEmail(); // load ra luôn từ main thread
        String userEmail = booking.getContactEmail().trim(); // load ra luôn từ main thread
        CompletableFuture.runAsync(() -> {
            try {
                if (bookingDto.getContactEmail() != null) {
                    String subject = "Xác nhận đặt vé tàu - Mã booking: " + bookingCode;
                    //    String emailContent = buildEmailContent(bookingDto, tickets);
                    String emailContent = TemplateMail.buildEmailHtmlContent(bookingDto, tickets);
                    //test send mail with qr
                    BufferedImage qrImage = generateQRCodeImage(qrBaseUrl + booking.getBookingCode());
                    byte[] qrBytes = bufferedImageToByteArray(qrImage, "PNG");
                    emailService.sendEmailWithQRCode(booking.getContactEmail(), "Thông tin vé tàu của bạn", emailContent, qrBytes);
                    //   emailService.sendEmail(userEmail, subject, emailContent);
                    log.info("Confirmation email sent successfully for booking: {}", bookingCode);
                    emailService.createSuccessNotification(bookingDto); // truyền DTO nếu cần
                }
            } catch (Exception e) {
                log.warn("Failed to send confirmation email for booking: {}", bookingCode, e);
                e.printStackTrace();
                emailService.createEmailFailureNotification(bookingDto); // truyền DTO nếu cần
                scheduleEmailRetry(bookingDto, 1);
            }
        });
    }

    /**
     * Scheduled task để xử lý các booking hết hạn hold
     */
    @Scheduled(fixedRate = 60000) // Chạy mỗi phút
    public void processExpiredHoldBookings() {
        try {
            LocalDateTime now = LocalDateTime.now();

            // Tìm các ticket đang hold và đã hết hạn
            List<Ticket> expiredTickets = ticketRepository.findByStatusAndHoldExpireTimeBefore(Ticket.Status.hold, now);

            if (!expiredTickets.isEmpty()) {
                // Group theo booking
                Map<Integer, List<Ticket>> ticketsByBooking = expiredTickets.stream().collect(Collectors.groupingBy(ticket -> ticket.getBooking().getBookingId()));

                for (Map.Entry<Integer, List<Ticket>> entry : ticketsByBooking.entrySet()) {
                    Integer bookingId = entry.getKey();
                    List<Ticket> tickets = entry.getValue();

                    try {
                        // Cập nhật booking status
                        Booking booking = bookingRepository.findById(bookingId).orElse(null);
                        if (booking != null && booking.getPaymentStatus() == Booking.PaymentStatus.pending) {
                            booking.setPaymentStatus(Booking.PaymentStatus.cancelled);
                            booking.setBookingStatus(Booking.BookingStatus.cancelled);
                            bookingRepository.save(booking);

                            // Cập nhật ticket status
                            for (Ticket ticket : tickets) {
                                ticket.setStatus(Ticket.Status.expired);
                            }
                            ticketRepository.saveAll(tickets);

                            // Giải phóng ghế
                            for (Ticket ticket : tickets) {
                                redisSeatLockService.unlockSeat(ticket.getTrip().getTripId(), ticket.getSeat().getSeatId());
                            }

                            // Rollback promotion
                            rollbackPromotionUsage(booking);

                            log.info("Processed expired booking: {}", booking.getBookingCode());
                        }
                    } catch (Exception e) {
                        log.error("Error processing expired booking: {}", bookingId, e);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error in processExpiredHoldBookings", e);
        }
    }

    @Transactional
    @Override
    public boolean cancelBookingByAdmin(Integer bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy booking" + bookingId));

        if (booking.getBookingStatus() == Booking.BookingStatus.cancelled || booking.getBookingStatus() == Booking.BookingStatus.completed) {
            return false; // Không được huỷ nếu đã hoàn tất
        }

        // Cập nhật trạng thái Booking
        booking.setBookingStatus(Booking.BookingStatus.cancelled);
        bookingRepository.save(booking);

        // Cập nhật tất cả ticket liên quan
        List<Ticket> tickets = ticketRepository.findAllByBookingBookingId(bookingId);
        for (Ticket ticket : tickets) {
            ticket.setStatus(Ticket.Status.cancelled);
        }
        ticketRepository.saveAll(tickets);
//        List<String> userEmails = tripRepository.findEffectiveEmailsByTripId(tickets.get(0).getTrip().getTripId());
        notificationService.notifyBookingCancellation(booking.getContactEmail(),booking,tickets.get(0).getTrip().getTripCode(), Constants.REASON_CANCELLED);
        User user= utils.getUser();
        SystemLog logg = SystemLog.builder()
                .user(user)
                .action(Constants.Action.UPDATE)
                .entityType("Booking")
                .entityId(Math.toIntExact(bookingId))
                .description("Cancel Status Booking: " + bookingId)
                //  .ipAddress(request.getRemoteAddr())
                //  .userAgent(request.getHeader("User-Agent"))
                //   .logTime(LocalDateTime.now())
                .build();

        systemLogRepository.save(logg);
        // log.debug("📘 Logged [{}] [{}:{}] - {}", logAction.action(), logAction.entity(), entityId, desString);
        systemLogService.logAction(logg);
        return true;
    }
    private double calculatePromotionDiscountRate(String promotionCode, double originalAmount) {
        Promotion promotion = promotionRepository.findByPromotionCodeAndStatus(promotionCode, Promotion.Status.active)
                .orElseThrow(() -> new RuntimeException("Invalid or inactive promotion code"));

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(promotion.getStartDate()) || now.isAfter(promotion.getEndDate())) {
            throw new RuntimeException("Promotion is expired or not active yet");
        }

        if (promotion.getUsageLimit() != null &&
                promotion.getUsageCount() != null &&
                promotion.getUsageCount() >= promotion.getUsageLimit()) {
            throw new RuntimeException("Promotion usage limit exceeded");
        }

        if (promotion.getMinimumPurchase() != null && originalAmount < promotion.getMinimumPurchase()) {
            throw new RuntimeException("Order does not meet minimum purchase requirement");
        }

        double discountAmount = 0.0;

        if (promotion.getDiscountType() == Promotion.DiscountType.percentage) {
            discountAmount = originalAmount * (promotion.getDiscountValue() / 100.0);
            if (promotion.getMaximumDiscount() != null) {
                discountAmount = Math.min(discountAmount, promotion.getMaximumDiscount());
            }
        } else if (promotion.getDiscountType() == Promotion.DiscountType.fixed_amount) {
            discountAmount = Math.min(promotion.getDiscountValue(), originalAmount); // không được âm
        }

        // Trả về tỉ lệ phần trăm đã giảm (để chia đều cho từng vé)
        return discountAmount / originalAmount;
    }

}
