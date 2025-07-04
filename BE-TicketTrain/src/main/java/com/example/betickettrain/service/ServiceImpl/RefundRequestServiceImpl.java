package com.example.betickettrain.service.ServiceImpl;

import com.example.betickettrain.dto.RefundRequestDto;
import com.example.betickettrain.dto.TicketDto;
import com.example.betickettrain.entity.*;
import com.example.betickettrain.exceptions.BusinessException;
import com.example.betickettrain.exceptions.ErrorCode;
import com.example.betickettrain.mapper.RefundRequestMapper;
import com.example.betickettrain.mapper.TicketMapper;
import com.example.betickettrain.repository.*;
import com.example.betickettrain.service.EmailService;
import com.example.betickettrain.service.RefundRequestService;
import com.example.betickettrain.util.utils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefundRequestServiceImpl implements RefundRequestService {
    private final TicketMapper ticketMapper;
    private final TicketRepository ticketRepository;
    private final RefundPolicyRepository refundPolicyRepository;
    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final EmailService emailService;
    private final BookingPromotionRepository bookingPromotionRepository;

    private final RefundRequestRepository refundRequestRepository;
    private final RefundRequestMapper refundRequestMapper;
    @Transactional
    @Override
    public void requestRefundBooking(Integer bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy booking: " + bookingId));

        if (booking.getBookingStatus() != Booking.BookingStatus.confirmed &&
                booking.getBookingStatus() != Booking.BookingStatus.completed) {
            throw new ResourceNotFoundException("Booking không hợp lệ để hoàn vé.");
        }

        List<Ticket> tickets = ticketRepository.findAllByBookingBookingId(booking.getBookingId());

        if (tickets.stream().anyMatch(t -> t.getStatus() != Ticket.Status.booked)) {
            throw new ResourceNotFoundException("Tất cả vé trong đơn phải ở trạng thái 'booked' để hoàn.");
        }

        // Lấy vé có giờ khởi hành sớm nhất
        Trip earliestTrip = tickets.stream()
                .map(Ticket::getTrip)
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy chuyến tàu."));

        long hoursLeft = Duration.between(LocalDateTime.now(), earliestTrip.getDepartureTime()).toHours();

        RefundPolicy policy = refundPolicyRepository
                .findAllByOrderByHoursBeforeDepartureDesc()
                .stream()
                .filter(p -> hoursLeft >= p.getHoursBeforeDeparture())
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.REFUND_POLICY.code, ErrorCode.REFUND_POLICY.message));

        double totalTicketPrice = tickets.stream().mapToDouble(Ticket::getTicketPrice).sum();

        BookingPromotion bookingPromotion = bookingPromotionRepository.findBookingPromotionByBooking_BookingId(booking.getBookingId());
        double refundAmount = 0;
        double netAmount=0;
        double totalDiscount=0;

        if(bookingPromotion!= null) {
             totalDiscount = bookingPromotion.getDiscountAmount();
            if (totalDiscount < 0 || totalDiscount > totalTicketPrice) {
                throw new BusinessException(ErrorCode.INVALID_DISCOUNT.code, "Giá trị giảm giá không hợp lệ.");
            }

             netAmount = totalTicketPrice - totalDiscount;
             refundAmount = netAmount * (policy.getRefundPercent() / 100.0);
        }


        // Cập nhật trạng thái các vé
        for (Ticket ticket : tickets) {
            ticket.setStatus(Ticket.Status.pending_refund);
        }
        ticketRepository.saveAll(tickets);

        booking.setBookingStatus(Booking.BookingStatus.refund_processing);
        bookingRepository.save(booking);

        Payment payment = paymentRepository.findByBooking(booking);
        payment.setStatus(Payment.Status.refund_pending);
        paymentRepository.save(payment);

        // TẠO REFUND REQUEST - PHẦN QUAN TRỌNG
        RefundRequest refundRequest = RefundRequest.builder()
                .booking(booking)
                .payment(payment)
                .refundPolicy(policy)
                .originalAmount(totalTicketPrice)
                .discountAmount(totalDiscount)
                .netAmount(netAmount)
                .refundAmount(refundAmount)
                .refundPercentage(policy.getRefundPercent())
                .hoursBeforeDeparture(hoursLeft)
                .status(RefundRequest.RefundStatus.pending)
                .build();

        refundRequestRepository.save(refundRequest);

        log.info("User {} yêu cầu hoàn booking {} với tổng tiền: {}, hoàn: {}",
                booking.getUser().getUserId(), booking.getBookingCode(), netAmount, refundAmount);

        emailService.sendRefundRequestedEmail(booking, tickets, refundAmount, policy);
    }


    @Override
    public Page<RefundRequestDto> getRefundRequests(String search, LocalDateTime fromDate, LocalDateTime toDate, Pageable pageable) {
        Specification<RefundRequest> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (search != null && !search.isBlank()) {
                String pattern = "%" + search.toLowerCase() + "%";
                Join<RefundRequest, Booking> bookingJoin = root.join("booking");
                predicates.add(cb.or(
                        cb.like(cb.lower(bookingJoin.get("bookingCode")), pattern),
                        cb.like(cb.lower(bookingJoin.get("contactEmail")), pattern),
                        cb.like(cb.lower(bookingJoin.get("contactPhone")), pattern)
                ));
            }

            if (fromDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("requestDate"), fromDate));
            }

            if (toDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("requestDate"), toDate));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<RefundRequest> result = refundRequestRepository.findAll(spec, pageable);

        // 👉 Map sang DTO và bổ sung thông tin chuyến
        List<RefundRequestDto> dtoList = result.getContent().stream().map(refundRequest -> {
            RefundRequestDto dto = refundRequestMapper.toDto(refundRequest);

            List<Ticket> tickets = ticketRepository.findByBookingBookingId(refundRequest.getBooking().getBookingId());

            if (!tickets.isEmpty()) {
                Ticket ticket = tickets.get(0);
                dto.setTripCode(ticket.getTrip().getTripCode());
                dto.setRouteName(ticket.getTrip().getRoute().getRouteName());
                dto.setDepartureTime(ticket.getTrip().getDepartureTime());
                dto.setOriginStationName(ticket.getTrip().getRoute().getOriginStation().getStationName());
                dto.setDestinationStationName(ticket.getTrip().getRoute().getDestinationStation().getStationName());
            }

            return dto;
        }).toList();

        return new PageImpl<>(dtoList, pageable, result.getTotalElements());
    }

    @Override
    public RefundRequestDto getRefundRequestById(Long refundRequestId) {
        RefundRequest refundRequest = refundRequestRepository.findById(refundRequestId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy yêu cầu hoàn tiền #" + refundRequestId));

        RefundRequestDto dto = refundRequestMapper.toDto(refundRequest);

        // 🧠 Lấy Ticket để truy ngược lên Trip
        List<Ticket> tickets = ticketRepository.findByBookingBookingId(refundRequest.getBooking().getBookingId());

        if (!tickets.isEmpty()) {
            Ticket ticket = tickets.get(0); // Giả sử Booking chỉ có 1 Trip
            dto.setTripCode(ticket.getTrip().getTripCode());
            dto.setRouteName(ticket.getTrip().getRoute().getRouteName());
            dto.setDepartureTime(ticket.getTrip().getDepartureTime());
            dto.setOriginStationName(ticket.getTrip().getRoute().getOriginStation().getStationName());
            dto.setDestinationStationName(ticket.getTrip().getRoute().getDestinationStation().getStationName());
        }

        return dto;
    }
    @Override
    public void approveRefundRequest(Long refundRequestId, String adminNote ) {
        RefundRequest request = refundRequestRepository.findById(refundRequestId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy yêu cầu hoàn tiền."));

        if (request.getStatus() != RefundRequest.RefundStatus.pending) {
            throw new BusinessException("A10","Yêu cầu đã được xử lý."); //tam
        }
         User user = utils.getUser();
        Booking booking = request.getBooking();
        Payment payment = request.getPayment();

        // Cập nhật trạng thái refund
        request.setStatus(RefundRequest.RefundStatus.approved);
        request.setAdminNote(adminNote);
        request.setProcessedDate(LocalDateTime.now());
        request.setProcessedBy(Objects.requireNonNull(user).getUsername()); // TODO: Lấy ID admin hiện tại nếu có auth

        // Cập nhật payment
        payment.setStatus(Payment.Status.refunded);
        paymentRepository.save(payment);

        // Cập nhật booking
        booking.setBookingStatus(Booking.BookingStatus.refunded);
        booking.setPaymentStatus(Booking.PaymentStatus.refunded);
        bookingRepository.save(booking);

        // Cập nhật ticket -> cancelled
        List<Ticket> tickets = ticketRepository.findAllByBookingBookingId(booking.getBookingId());
        for (Ticket ticket : tickets) {
            if (ticket.getStatus() == Ticket.Status.pending_refund) {
                ticket.setStatus(Ticket.Status.cancelled);
            }
        }
        ticketRepository.saveAll(tickets);

        refundRequestRepository.save(request);

        log.info("RefundRequest {} đã được duyệt", refundRequestId);
        List<TicketDto> ticketDtoList = tickets.stream().map(ticketMapper::toDto).toList();
        // Gửi email xác nhận
        emailService.sendRefundApprovedEmail(booking, ticketDtoList, request.getRefundAmount());
    }

    @Override
    public void rejectRefundRequest(Long refundRequestId, String reason) {
        RefundRequest request = refundRequestRepository.findById(refundRequestId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy yêu cầu hoàn tiền."));

        if (request.getStatus() != RefundRequest.RefundStatus.pending) {
            throw new BusinessException("A10","Yêu cầu đã được xử lý.");
        }

        Booking booking = request.getBooking();
        Payment payment = request.getPayment();

        // Cập nhật trạng thái refund
        request.setStatus(RefundRequest.RefundStatus.rejected);
        request.setRejectionReason(reason);
        request.setProcessedDate(LocalDateTime.now());
        request.setProcessedBy(Objects.requireNonNull(utils.getUser()).getUsername()); // TODO: Lấy ID admin

        // Trả lại trạng thái cho booking
        booking.setBookingStatus(Booking.BookingStatus.confirmed);
        bookingRepository.save(booking);

        // Trả lại trạng thái cho payment
        payment.setStatus(Payment.Status.completed);
        paymentRepository.save(payment);

        // Cập nhật ticket về lại booked
        List<Ticket> tickets = ticketRepository.findAllByBookingBookingId(booking.getBookingId());
        for (Ticket ticket : tickets) {
            if (ticket.getStatus() == Ticket.Status.pending_refund) {
                ticket.setStatus(Ticket.Status.booked);
            }
        }
        ticketRepository.saveAll(tickets);

        refundRequestRepository.save(request);
        List<TicketDto> ticketDtoList = tickets.stream().map(ticketMapper::toDto).toList();
        log.info("RefundRequest {} đã bị từ chối", refundRequestId);

        // Gửi email từ chối
        emailService.sendRefundRejectedEmail(booking, ticketDtoList, reason);
    }

}
