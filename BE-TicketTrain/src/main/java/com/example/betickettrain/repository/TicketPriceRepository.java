package com.example.betickettrain.repository;

import com.example.betickettrain.entity.TicketPrice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketPriceRepository extends JpaRepository<TicketPrice, Integer> {
}