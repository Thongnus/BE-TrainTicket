package com.example.betickettrain.repository;

import com.example.betickettrain.entity.TicketPrice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
public interface TicketPriceRepository extends JpaRepository<TicketPrice, Integer> {
    List<TicketPrice> findByRouteRouteId(Integer routeRouteId);
}