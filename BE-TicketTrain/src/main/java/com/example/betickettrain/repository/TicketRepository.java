package com.example.betickettrain.repository;

import com.example.betickettrain.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
public interface TicketRepository extends JpaRepository<Ticket, Integer> {


    List<Ticket> findAllByTrip_TripId(Integer tripTripId);
}