package com.example.betickettrain.repository;

import com.example.betickettrain.entity.Booking;
import com.example.betickettrain.entity.BookingPromotion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
public interface BookingPromotionRepository extends JpaRepository<BookingPromotion, Integer> {
    List<BookingPromotion> findAllByBookingBookingId(Integer bookingBookingId);


    BookingPromotion findBookingPromotionByBooking_BookingId(Integer bookingBookingId);
}