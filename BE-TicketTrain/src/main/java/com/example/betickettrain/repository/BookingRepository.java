package com.example.betickettrain.repository;


import com.example.betickettrain.dto.BookingDto;
import com.example.betickettrain.entity.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Integer>, JpaSpecificationExecutor<Booking> {
    Optional<Booking> findByBookingCode(String bookingCode);


    Page<Booking> findAllByUser_UserIdAndPaymentStatusIn(Long userId, List<Booking.PaymentStatus> statuses, Pageable pageable);

    @Query("SELECT COALESCE(SUM(b.totalAmount), 0) FROM Booking b WHERE b.paymentStatus = 'paid' AND b.bookingDate BETWEEN :start AND :end")
    double sumRevenue(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(b) FROM Ticket b WHERE  b.status in ('booked', 'checked_in', 'used') and b.createdAt BETWEEN :start AND :end")
    int countTickets(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(b) FROM Ticket b WHERE b.status = 'cancelled' AND b.createdAt BETWEEN :start AND :end")
    int countCancelledTickets(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);


}