package com.example.betickettrain.repository;

import com.example.betickettrain.entity.TicketChange;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketChangeRepository extends JpaRepository<TicketChange, Integer> {
}