package com.example.betickettrain.repository;


import com.example.betickettrain.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Integer>, JpaSpecificationExecutor<Booking> {
    Optional<Booking> findByBookingCode(String bookingCode);

//    @Query("""
//    SELECT new com.example.betickettrain.dto.BookingFlatDTO(
//        b.bookingId,
//        b.bookingCode,
//        b.bookingStatus,
//        FUNCTION('DATE_FORMAT', b.createdAt, '%Y-%m-%d %H:%i:%s'),
//        b.totalAmount,
//        tr.trainNumber,
//        t.departureTime,
//        t.arrivalTime,
//        os.stationName,
//        ds.stationName,
//        b.paymentMethod,
//        b.paymentStatus,
//        b.bookingCode,  \s
//        FUNCTION('DATE_FORMAT', b.paymentDate, '%Y-%m-%d %H:%i:%s')
//    )
//    FROM Booking b
//    JOIN Ticket tk ON tk.booking.bookingId = b.bookingId
//    JOIN Trip t ON tk.trip.tripId = t.tripId
//    JOIN t.route r
//    JOIN r.originStation os
//    JOIN r.destinationStation ds
//    JOIN t.train tr
//    WHERE b.user.userId = :userId
//    GROUP BY b.bookingId
//""")
//    List<BookingFlatDTO> findBookingWithTripInfo(Long userId);

//
//    @Query("""
//        SELECT new com.example.betickettrain.dto.BookingFlatDTO(
//            b.bookingId,
//            b.bookingCode,
//            b.bookingStatus,
//            b.createdAt,
//            b.totalAmount,
//            tr.trainNumber,
//            t.departureTime,
//            t.arrivalTime,
//            os.stationName,
//            ds.stationName,
//            tk.passengerName,
//            tk.passengerIdCard,
//            s.seatNumber,
//            b.paymentMethod,
//            b.paymentStatus,
//            b.paymentDate
//        )
//        FROM Booking b
//        LEFT JOIN Ticket tk ON tk.booking.bookingId = b.bookingId
//        LEFT JOIN Trip t ON tk.trip.tripId = t.tripId
//        LEFT JOIN Route r ON t.route = r
//        LEFT JOIN Station os ON r.originStation = os
//        LEFT JOIN Station ds ON r.destinationStation = ds
//        LEFT JOIN Train tr ON t.train = tr
//        LEFT JOIN Seat s ON tk.seat = s
//        WHERE b.user.userId = :userId
//        ORDER BY b.bookingId, tk.ticketId
//    """)
//    List<BookingFlatDTO> findBookingWithDetails(Long userId);


    List<Booking> findAllByUser_UserId(Long userUserId);
}