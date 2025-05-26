package com.example.betickettrain.repository;

import com.example.betickettrain.entity.BookingPromotion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface BookingPromotionRepository extends JpaRepository<BookingPromotion, Integer> {
    List<BookingPromotion> findByBookingBookingId(Integer bookingBookingId);
}