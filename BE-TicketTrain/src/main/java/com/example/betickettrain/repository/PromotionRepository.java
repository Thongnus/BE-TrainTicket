package com.example.betickettrain.repository;

import com.example.betickettrain.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PromotionRepository extends JpaRepository<Promotion, Integer> {

    Optional<Promotion> findByPromotionCodeAndStatus(String promotionCode, Promotion.Status status);
}