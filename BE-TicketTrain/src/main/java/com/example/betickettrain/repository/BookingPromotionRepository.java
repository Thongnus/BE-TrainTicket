package com.example.betickettrain.repository;

import com.example.betickettrain.entity.BookingPromotion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingPromotionRepository extends JpaRepository<BookingPromotion, Integer> {
}