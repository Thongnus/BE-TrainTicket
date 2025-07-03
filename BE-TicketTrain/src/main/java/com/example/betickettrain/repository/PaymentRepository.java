package com.example.betickettrain.repository;

import com.example.betickettrain.entity.Booking;
import com.example.betickettrain.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {

    Payment findByBooking_BookingId(Integer bookingBookingId);

    List<Payment> findAllByBooking_BookingId(Integer bookingBookingId);

    Payment findByBooking(Booking booking);
}