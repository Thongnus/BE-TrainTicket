package com.example.betickettrain.service.ServiceImpl;

import com.example.betickettrain.dto.BookingDto;
import com.example.betickettrain.entity.*;
import com.example.betickettrain.exceptions.BusinessException;
import com.example.betickettrain.exceptions.ErrorCode;
import com.example.betickettrain.repository.*;
import com.example.betickettrain.service.EmailService;
import com.example.betickettrain.service.RefundPolicyService;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class RefundPolicyServiceImpl implements RefundPolicyService {

    private final TicketRepository ticketRepository;
    private final RefundPolicyRepository refundPolicyRepository;
    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final EmailService emailService;
    private final BookingPromotionRepository bookingPromotionRepository;
//    @Transactional
//    public void requestRefundTicket(Integer ticketId) {
//        Ticket ticket = ticketRepository.findById(ticketId)
//                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy vé: "+ticketId));
//
//        // 1. Kiểm tra vé có thể hoàn
//        if (ticket.getStatus() != Ticket.Status.booked) {
//            throw new IllegalStateException("Vé không thể hoàn trong trạng thái: " + ticket.getStatus());
//        }
//
//        Trip trip = ticket.getTrip();
//        Duration timeLeft = Duration.between(LocalDateTime.now(), trip.getDepartureTime());
//        long hoursLeft = timeLeft.toHours();
//
//        // 2. Tìm chính sách hoàn phù hợp
//        RefundPolicy policy = refundPolicyRepository
//                .findAllByOrderByHoursBeforeDepartureDesc()
//                .stream()
//                .filter(p -> hoursLeft >= p.getHoursBeforeDeparture())
//                .findFirst()
//                .orElseThrow(() -> new BusinessException(ErrorCode.REFUND_POLICY.code, ErrorCode.REFUND_POLICY.message));
//
//        // 3. Tính toán hoàn tiền
//        double refundAmount = ticket.getTicketPrice() * (policy.getRefundPercent() / 100.0);
//
//        // 4. Cập nhật trạng thái
//        ticket.setStatus(Ticket.Status.cancelled);
//        ticketRepository.save(ticket);
//
//        Booking booking = ticket.getBooking();
//        booking.setBookingStatus(Booking.BookingStatus.refund_processing);
//        bookingRepository.save(booking);
//
//        Payment payment = paymentRepository.findByBooking(booking);
//        payment.setStatus(Payment.Status.refund_pending);
//        payment.setRefundRequestAt(LocalDateTime.now());
//        paymentRepository.save(payment);
//
//        // 5. Lưu note hoặc log
//        log.info("User {} yêu cầu hoàn vé {} với chính sách {}", booking.getUser().getUserId(), ticket.getTicketCode(), policy.getPolicyName());
//
//        // 6. Gửi email/thông báo
//        emailService.sendRefundRequestedEmail(booking, ticket, refundAmount, policy);
//    }
//@Transactional
//@Override
//public void requestRefundBooking(Integer bookingId) {
//    Booking booking = bookingRepository.findById(bookingId)
//            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy booking: " + bookingId));
//
//    if (booking.getBookingStatus() != Booking.BookingStatus.confirmed &&
//            booking.getBookingStatus() != Booking.BookingStatus.completed) {
//        throw new ResourceNotFoundException("Booking không hợp lệ để hoàn vé.");
//    }
//
//    List<Ticket> tickets = ticketRepository.findAllByBookingBookingId(booking.getBookingId());
//
//    if (tickets.stream().anyMatch(t -> t.getStatus() != Ticket.Status.booked)) {
//        throw new ResourceNotFoundException("Tất cả vé trong đơn phải ở trạng thái 'booked' để hoàn.");
//    }
//
//    // Lấy vé có giờ khởi hành sớm nhất
//    Trip earliestTrip = tickets.stream()
//            .map(Ticket::getTrip)
//            .findFirst()
//            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy chuyến tàu."));
//
//    long hoursLeft = Duration.between(LocalDateTime.now(), earliestTrip.getDepartureTime()).toHours();
//
//    RefundPolicy policy = refundPolicyRepository
//            .findAllByOrderByHoursBeforeDepartureDesc()
//            .stream()
//            .filter(p -> hoursLeft >= p.getHoursBeforeDeparture())
//            .findFirst()
//            .orElseThrow(() -> new BusinessException(ErrorCode.REFUND_POLICY.code, ErrorCode.REFUND_POLICY.message));
//
//    double totalTicketPrice = tickets.stream().mapToDouble(Ticket::getTicketPrice).sum();
//
//    double totalDiscount = bookingPromotionRepository.findBookingPromotionByBooking_BookingId(booking.getBookingId()).getDiscountAmount();
//    if (totalDiscount < 0 || totalDiscount > totalTicketPrice) {
//        throw new BusinessException(ErrorCode.INVALID_DISCOUNT.code, "Giá trị giảm giá không hợp lệ.");
//    }
//
//    double netAmount = totalTicketPrice - totalDiscount;
//    double refundAmount = netAmount * (policy.getRefundPercent() / 100.0);
//
//    // Cập nhật trạng thái các vé
//    for (Ticket ticket : tickets) {
//        ticket.setStatus(Ticket.Status.cancelled);
//    }
//    ticketRepository.saveAll(tickets);
//
//    booking.setBookingStatus(Booking.BookingStatus.refund_processing);
//    booking.setPaymentStatus(Booking.PaymentStatus.refund_pending);
//    bookingRepository.save(booking);
//
//    Payment payment = paymentRepository.findByBooking(booking);
//    payment.setStatus(Payment.Status.refund_pending);
//    payment.setRefundRequestAt(LocalDateTime.now());
//    paymentRepository.save(payment);
//
//    log.info("User {} yêu cầu hoàn booking {} với tổng tiền: {}, hoàn: {}", booking.getUser().getUserId(), booking.getBookingCode(), netAmount, refundAmount);
//
//    emailService.sendRefundRequestedEmail(booking, tickets, refundAmount, policy);
//}
//
//    @Override
//    public Page<BookingDto> getRefundRequests(String search, LocalDateTime fromDate, LocalDateTime toDate, Pageable pageable) {
//        Specification<Booking> spec = (root, query, cb) -> {
//            List<Predicate> predicates = new ArrayList<>();
//
//            predicates.add(root.get("bookingStatus").in(List.of(
//                    Booking.BookingStatus.refund_processing,
//                    Booking.BookingStatus.refund_failed,
//                    Booking.BookingStatus.cancelled
//            )));
//
//            predicates.add(root.get("paymentStatus").in(List.of(
//                    Booking.PaymentStatus.refund_pending,
//                    Booking.PaymentStatus.refunded,
//                    Booking.PaymentStatus.refund_failed
//            )));
//
//            if (search != null && !search.isBlank()) {
//                String pattern = "%" + search.toLowerCase() + "%";
//                predicates.add(cb.or(
//                        cb.like(cb.lower(root.get("bookingCode")), pattern),
//                        cb.like(cb.lower(root.get("contactEmail")), pattern),
//                        cb.like(cb.lower(root.get("contactPhone")), pattern)
//                ));
//            }
//
//            if (fromDate != null) {
//                predicates.add(cb.greaterThanOrEqualTo(root.get("updatedAt"), fromDate));
//            }
//
//            if (toDate != null) {
//                predicates.add(cb.lessThanOrEqualTo(root.get("updatedAt"), toDate));
//            }
//
//
//            Predicate[] predicateArray = predicates.toArray(new Predicate[0]);
//            return cb.and(predicateArray);
//        };
//
//        return bookingRepository.findAll(spec, pageable);
//    }
//
//    //    @Transactional
//    //    public void requestRefundTicket(Integer ticketId) {
//    //        Ticket ticket = ticketRepository.findById(ticketId)
//    //                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy vé: "+ticketId));
//    //
//    //        // 1. Kiểm tra vé có thể hoàn
//    //        if (ticket.getStatus() != Ticket.Status.booked) {
//    //            throw new IllegalStateException("Vé không thể hoàn trong trạng thái: " + ticket.getStatus());
//    //        }
//    //
//    //        Trip trip = ticket.getTrip();
//    //        Duration timeLeft = Duration.between(LocalDateTime.now(), trip.getDepartureTime());
//    //        long hoursLeft = timeLeft.toHours();
//    //
//    //        // 2. Tìm chính sách hoàn phù hợp
//    //        RefundPolicy policy = refundPolicyRepository
//    //                .findAllByOrderByHoursBeforeDepartureDesc()
//    //                .stream()
//    //                .filter(p -> hoursLeft >= p.getHoursBeforeDeparture())
//    //                .findFirst()
//    //                .orElseThrow(() -> new BusinessException(ErrorCode.REFUND_POLICY.code, ErrorCode.REFUND_POLICY.message));
//    //
//    //        // 3. Tính toán hoàn tiền
//    //        double refundAmount = ticket.getTicketPrice() * (policy.getRefundPercent() / 100.0);
//    //
//    //        // 4. Cập nhật trạng thái
//    //        ticket.setStatus(Ticket.Status.cancelled);
//    //        ticketRepository.save(ticket);
//    //
//    //        Booking booking = ticket.getBooking();
//    //        booking.setBookingStatus(Booking.BookingStatus.refund_processing);
//    //        bookingRepository.save(booking);
//    //
//    //        Payment payment = paymentRepository.findByBooking(booking);
//    //        payment.setStatus(Payment.Status.refund_pending);
//    //        payment.setRefundRequestAt(LocalDateTime.now());
//    //        paymentRepository.save(payment);
//    //
//    //        // 5. Lưu note hoặc log
//    //        log.info("User {} yêu cầu hoàn vé {} với chính sách {}", booking.getUser().getUserId(), ticket.getTicketCode(), policy.getPolicyName());
//    //
//    //        // 6. Gửi email/thông báo
//    //        emailService.sendRefundRequestedEmail(booking, ticket, refundAmount, policy);
//    //    }
}
