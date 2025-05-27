package com.example.betickettrain.service.ServiceImpl;

import com.example.betickettrain.dto.BookingCheckoutRequest;
import com.example.betickettrain.dto.BookingLockRequest;
import com.example.betickettrain.dto.PassengerTicketDto;
import com.example.betickettrain.entity.*;
import com.example.betickettrain.repository.*;
import com.example.betickettrain.service.BookingService;
import com.example.betickettrain.service.EmailService;
import com.example.betickettrain.service.RedisSeatLockService;
import com.example.betickettrain.util.DateUtils;
import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
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

    // chưa hoàn thiện vẫn chưa fix dc race condition
    @Override
    public void lockSeats(BookingLockRequest request) {
        // Lấy danh sách các ghế đang bị lock cho chuyến này
        Set<Integer> lockedSeats = redisSeatLockService.getLockedSeats(request.getTripId());

        // Kiểm tra xem có ghế nào trong request đã bị lock chưa
        for (Integer seatId : request.getSeatIds()) {
            if (lockedSeats.contains(seatId)) {
                throw new RuntimeException("Một hoặc nhiều ghế đã bị khóa trước đó, không thể đặt.");
            }
        }

        // Nếu tất cả ghế đều chưa bị lock, tiến hành lock
        for (Integer seatId : request.getSeatIds()) {
            boolean locked = redisSeatLockService.tryLockSeat(request.getTripId(), seatId, Duration.ofMinutes(8));
            if (!locked) {
                throw new RuntimeException("Ghế " + seatId + " đã bị khóa.");
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

        User userRepositoryById = userRepository.findById(user.getUserId());
                // Bước 1: Kiểm tra và gia hạn lock
                BookingLockRequest lockRequest = new BookingLockRequest();
        lockRequest.setTripId(request.getTripId());
        List<Integer> seatIds = request.getPassengerTickets().stream()
                .map(PassengerTicketDto::getSeatId)
                .toList();

        for (Integer seatId : seatIds) {
            redisSeatLockService.extendSeatLock(request.getTripId(), seatId, Duration.ofMinutes(15));
        }

        try {
            // Bước 2: Lấy trip và route
            Trip trip = tripRepository.findById(request.getTripId())
                    .orElseThrow(() -> new RuntimeException("Trip not found"));
            Route route = trip.getRoute();
            if (route == null) throw new RuntimeException("Route not found for trip");

            // Bước 3: Load all seats và map theo seatId
            Map<Integer, Seat> seatMap = seatRepository.findBySeatIdIn(seatIds).stream()
                    .collect(Collectors.toMap(Seat::getSeatId, s -> s));

            // Bước 4: Load ticketPrices theo route + loại toa
            List<Carriage.CarriageType> carriageTypes = seatMap.values().stream()
                    .map(seat -> seat.getCarriage().getCarriageType())
                    .collect(Collectors.toList());

            Map<Carriage.CarriageType, TicketPrice> priceMap =
                    ticketPriceRepository.findByRouteAndCarriageTypeAndDateRange(
                                    route.getRouteId(), carriageTypes, trip.getDepartureTime().toLocalDate()
                            ).stream()
                            .collect(Collectors.toMap(TicketPrice::getCarriageType, tp -> tp));

            // Bước 5: Tạo booking
            Booking booking = new Booking();
            booking.setUser(userRepositoryById);
            booking.setBookingCode("BK" + DateUtils.toString(LocalDateTime.now()));
            booking.setBookingStatus(Booking.BookingStatus.pending);
            booking.setPaymentMethod(Booking.PaymentMethod.valueOf(request.getPaymentMethod()));
            booking.setPaymentStatus(Booking.PaymentStatus.pending);
            booking.setCreatedAt(LocalDateTime.now());
            booking.setTotalAmount(0.0);
            booking = bookingRepository.save(booking);

            // Bước 6: Tạo ticket
            double totalBeforePromotion = 0;
            for (PassengerTicketDto pt : request.getPassengerTickets()) {
                Seat seat = seatMap.get(pt.getSeatId());
                if (seat == null) throw new RuntimeException("Seat not found: " + pt.getSeatId());

                TicketPrice price = priceMap.get(seat.getCarriage().getCarriageType());
                if (price == null)
                    throw new RuntimeException("No price for carriage type: " + seat.getCarriage().getCarriageType());

                double ticketPrice = calculateDynamicPrice(price, trip.getDepartureTime());
                String ticketCode = generateTicketCode();

                Ticket ticket = Ticket.builder()
                        .booking(booking)
                        .trip(trip)
                        .seat(seat)
                        .originStation(route.getOriginStation())
                        .destinationStation(route.getDestinationStation())
                        .passengerName(pt.getPassengerName())
                        .passengerIdCard(pt.getIdentityCard())
                        .ticketPrice(ticketPrice)
                        .ticketCode(ticketCode)
                        .status(Ticket.Status.hold)
                        .holdExpireTime(LocalDateTime.now().plusMinutes(15))
                        .createdAt(LocalDateTime.now())
                        .build();

                ticketRepository.save(ticket);
                totalBeforePromotion += ticketPrice;
            }

            // Bước 7: Áp dụng khuyến mãi nếu có
            double totalAfterPromotion = totalBeforePromotion;
            if (request.getPromotionCode() != null && !request.getPromotionCode().isBlank()) {
                totalAfterPromotion = applyPromotion(booking, request.getPromotionCode(), totalBeforePromotion);
            }

            booking.setTotalAmount(totalAfterPromotion);
            bookingRepository.save(booking);

            // Bước 8: Gửi URL thanh toán
            return "VNPAY".equalsIgnoreCase(request.getPaymentMethod())
                    ? vnpayService.generatePaymentUrl(booking)
                    : "/payment-success-local?bookingCode=" + booking.getBookingCode();

        } catch (Exception e) {
            // Giải phóng lock nếu lỗi
            seatIds.forEach(seatId -> redisSeatLockService.unlockSeat(request.getTripId(), seatId));
            throw e;
        }
    }


    @Override
    public boolean handleVnPayCallback(String bookingCode, String responseCode) {
        try {
            // Tìm booking theo booking code
            Booking booking = bookingRepository.findByBookingCode(bookingCode)
                    .orElseThrow(() -> new RuntimeException("Booking not found with code: " + bookingCode));

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
            log.error("Error handling VnPay callback for booking: {}", bookingCode, e);
            return false;
        }
    }

    /**
     * Tính giá vé dựa trên route, seat và trip
     */
//    private double calculateTicketPrice(Route route, Seat seat, Trip trip) {
//        // Lấy carriage type từ seat
//        Carriage.CarriageType carriageType = seat.getCarriage().getCarriageType();
//
//        // Lấy giá cơ bản từ bảng ticket_prices theo route và carriage type
//        TicketPrice ticketPrice = ticketPriceRepository
//                .findByRouteAndCarriageTypeAndDateRange(route.getRouteId(), carriageType, trip.getDepartureTime().toLocalDate())
//                .orElseThrow(() -> new RuntimeException("Ticket price not found for route: " + route.getRouteId() +
//                        " and carriage type: " + carriageType));
//
//        double finalPrice = ticketPrice.getBasePrice();
//
//        // Áp dụng phụ phí cuối tuần
//        if (isWeekend(trip.getDepartureTime().toLocalDate())) {
//            finalPrice += ticketPrice.getWeekendSurcharge() != null ? ticketPrice.getWeekendSurcharge() : 0;
//        }
//
//        // Áp dụng phụ phí lễ (cần implement logic check holiday)
//        if (isHoliday(trip.getDepartureTime().toLocalDate())) {
//            finalPrice += ticketPrice.getHolidaySurcharge() != null ? ticketPrice.getHolidaySurcharge() : 0;
//        }
//
//        // Áp dụng phụ phí giờ cao điểm
//        if (isPeakHour(trip.getDepartureTime().toLocalTime())) {
//            finalPrice += ticketPrice.getPeakHourSurcharge() != null ? ticketPrice.getPeakHourSurcharge() : 0;
//        }
//
//        // Áp dụng discount rate nếu có
//        if (ticketPrice.getDiscountRate() != null && ticketPrice.getDiscountRate() > 0) {
//            finalPrice = finalPrice * (1 - ticketPrice.getDiscountRate() / 100);
//        }
//
//        return finalPrice;
//    }
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
        return (time.isAfter(LocalTime.of(6, 0)) && time.isBefore(LocalTime.of(9, 0))) ||
                (time.isAfter(LocalTime.of(17, 0)) && time.isBefore(LocalTime.of(20, 0)));
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
        Promotion promotion = promotionRepository.findByPromotionCodeAndStatus(promotionCode, Promotion.Status.active)
                .orElseThrow(() -> new RuntimeException("Invalid or inactive promotion code"));

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
        BookingPromotion bookingPromotion = BookingPromotion.builder()
                .booking(booking)
                .promotion(promotion)
                .discountAmount(discountAmount)
                .appliedAt(LocalDateTime.now())
                .build();
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
    private boolean handleSuccessfulPayment(Booking booking) {
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
            }
            ticketRepository.saveAll(tickets);

            // Giải phóng lock ghế (vì đã book thành công)
            for (Ticket ticket : tickets) {
                redisSeatLockService.unlockSeat(ticket.getTrip().getTripId(), ticket.getSeat().getSeatId());
            }

            // Gửi email xác nhận (nếu có)
            try {
                sendBookingConfirmationEmail(booking);
            } catch (Exception e) {
                log.warn("Failed to send confirmation email for booking: {}", booking.getBookingCode(), e);
                // Không throw exception vì thanh toán đã thành công
            }

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
    private boolean handleFailedPayment(Booking booking, String responseCode) {
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
    private void rollbackPromotionUsage(Booking booking) {
        try {
            List<BookingPromotion> bookingPromotions = bookingPromotionRepository.findByBookingBookingId(booking.getBookingId());

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

    /**
     * Gửi email xác nhận booking
     */
    private void sendBookingConfirmationEmail(Booking booking) throws MessagingException {
        if (booking.getUser() != null && booking.getUser().getEmail() != null) {
            try {
                // Lấy thông tin tickets
                List<Ticket> tickets = ticketRepository.findByBookingBookingId(booking.getBookingId());

                // Tạo email content
                String emailSubject = "Xác nhận đặt vé tàu - Mã booking: " + booking.getBookingCode();
                String emailContent = buildEmailContent(booking, tickets);

                // Gửi email (cần implement emailService)
                emailService.sendEmail(booking.getUser().getEmail(), emailSubject, emailContent);

            } catch (Exception e) {
                log.error("Error sending confirmation email for booking: {}", booking.getBookingCode(), e);
                throw e;
            }
        }
    }

    /**
     * Tạo nội dung email
     */
    private String buildEmailContent(Booking booking, List<Ticket> tickets) {
        StringBuilder content = new StringBuilder();
        content.append("Kính chào ").append(booking.getUser().getFullName()).append(",\n\n");
        content.append("Cảm ơn bạn đã sử dụng dịch vụ đặt vé tàu của chúng tôi.\n");
        content.append("Thông tin đặt vé của bạn:\n\n");
        content.append("Mã booking: ").append(booking.getBookingCode()).append("\n");
        content.append("Ngày đặt: ").append(booking.getBookingDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))).append("\n");
        content.append("Tổng tiền: ").append(String.format("%,.0f VNĐ", booking.getTotalAmount())).append("\n");
        content.append("Trạng thái: Đã thanh toán\n\n");

        content.append("Chi tiết vé:\n");
        for (Ticket ticket : tickets) {
            content.append("- Hành khách: ").append(ticket.getPassengerName()).append("\n");
            content.append("  Chuyến: ").append(ticket.getTrip().getTripCode()).append("\n");
            content.append("  Tàu: ").append(ticket.getTrip().getTrain().getTrainNumber()).append("\n");
            content.append("  Ghế: ").append(ticket.getSeat().getSeatNumber()).append("\n");
            content.append("  Toa: ").append(ticket.getSeat().getCarriage().getCarriageNumber()).append("\n");
            content.append("  Giá vé: ").append(String.format("%,.0f VNĐ", ticket.getTicketPrice())).append("\n");
            content.append("  Mã vé: ").append(ticket.getTicketCode()).append("\n\n");
        }

        content.append("Vui lòng lưu lại email này để làm thủ tục lên tàu.\n");
        content.append("Trân trọng,\n");
        content.append("Đội ngũ hỗ trợ khách hàng");

        return content.toString();
    }

    /**
     * Scheduled task để xử lý các booking hết hạn hold
     */
    @Scheduled(fixedRate = 60000) // Chạy mỗi phút
    public void processExpiredHoldBookings() {
        try {
            LocalDateTime now = LocalDateTime.now();

            // Tìm các ticket đang hold và đã hết hạn
            List<Ticket> expiredTickets = ticketRepository.findByStatusAndHoldExpireTimeBefore(
                    Ticket.Status.hold, now);

            if (!expiredTickets.isEmpty()) {
                // Group theo booking
                Map<Integer, List<Ticket>> ticketsByBooking = expiredTickets.stream()
                        .collect(Collectors.groupingBy(ticket -> ticket.getBooking().getBookingId()));

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
                                redisSeatLockService.unlockSeat(ticket.getTrip().getTripId(),
                                        ticket.getSeat().getSeatId());
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
}
