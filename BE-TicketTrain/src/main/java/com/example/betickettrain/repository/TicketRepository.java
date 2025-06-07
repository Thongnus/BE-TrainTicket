package com.example.betickettrain.repository;

import com.example.betickettrain.dto.PassengerTicketDto;
import com.example.betickettrain.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
public interface TicketRepository extends JpaRepository<Ticket, Integer> {


    List<Ticket> findAllByTrip_TripId(Integer tripTripId);

    List<Ticket> findAllByBookingBookingId(Integer bookingBookingId);

    List<Ticket> findByBookingBookingId(Integer bookingBookingId);

    List<Ticket> findByStatusAndHoldExpireTimeBefore(Ticket.Status status, LocalDateTime holdExpireTimeBefore);
}