package com.example.betickettrain.service.ServiceImpl;

import com.example.betickettrain.dto.BookingCheckoutRequest;
import com.example.betickettrain.dto.BookingDto;
import com.example.betickettrain.dto.BookingLockRequest;
import com.example.betickettrain.entity.Booking;
import com.example.betickettrain.entity.Trip;
import com.example.betickettrain.repository.TripRepository;
import com.example.betickettrain.service.BookingService;
import com.example.betickettrain.service.RedisSeatLockService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@AllArgsConstructor
public class BookingServiceImpl  implements BookingService {
    private final RedisSeatLockService redisSeatLockService;
    private final TripRepository tripRepository;
    @Override
    public void lockSeats(BookingLockRequest request) {
        for (Integer seatId : request.getSeatIds()) {
            boolean locked = redisSeatLockService.tryLockSeat(request.getTripId(), seatId, Duration.ofMinutes(8));
            if (!locked) {
                throw new RuntimeException("Seat " + seatId + " is already locked.");
            }
        }
    }

    @Override
    public String initiateCheckout(BookingCheckoutRequest request) {
        Trip trip = tripRepository.findById(request.getTripId())
                .orElseThrow(() -> new RuntimeException("Trip not found"));

        Booking booking = new Booking();
        booking.set(trip);
        booking.setBookingCode(UUID.randomUUID().toString().substring(0, 8));
        booking.setBookingStatus(Booking.Status.PENDING);
        booking.setPaymentMethod(request.getPaymentMethod());
        booking.setPaymentStatus(Booking.PaymentStatus.PENDING);
        booking.setCreatedAt(LocalDateTime.now());
        booking.setTotalAmount(0.0);
        bookingRepository.save(booking);

        double total = 0;
        for (PassengerTicketDto pt : request.getPassengerTickets()) {
            Seat seat = seatRepository.findById(pt.getSeatId())
                    .orElseThrow(() -> new RuntimeException("Seat not found"));

            Ticket ticket = Ticket.builder()
                    .booking(booking)
                    .trip(trip)
                    .seat(seat)
                    .passengerName(pt.getPassengerName())
                    .passengerIdCard(pt.getIdentityCard())
                    .ticketPrice(400000.0)
                    .ticketStatus(Ticket.Status.hold)
                    .holdExpireTime(LocalDateTime.now().plusMinutes(15))
                    .createdAt(LocalDateTime.now())
                    .build();

            ticketRepository.save(ticket);
            total += ticket.getTicketPrice();
        }

        booking.setTotalAmount(total);
        bookingRepository.save(booking);

        if ("VNPAY".equalsIgnoreCase(request.getPaymentMethod())) {
            return vnpayService.generatePaymentUrl(booking);
        } else {
            return "/payment-success-local?bookingCode=" + booking.getBookingCode();
        }
    }
}
