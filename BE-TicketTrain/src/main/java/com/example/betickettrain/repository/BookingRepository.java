package com.example.betickettrain.repository;

import com.example.betickettrain.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Integer>, JpaSpecificationExecutor<Booking> {
    Optional<Booking> findByBookingCode(String bookingCode);
}