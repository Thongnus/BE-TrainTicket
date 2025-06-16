package com.example.betickettrain.repository;

import com.example.betickettrain.dto.PassengerTicketDto;
import com.example.betickettrain.dto.PopularRouteDTO;
import com.example.betickettrain.dto.PopularRouteProjection;
import com.example.betickettrain.dto.TicketCarriageDistributionDTO;
import com.example.betickettrain.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface TicketRepository extends JpaRepository<Ticket, Integer> {


    List<Ticket> findAllByTrip_TripId(Integer tripTripId);

    List<Ticket> findAllByBookingBookingId(Integer bookingBookingId);

    List<Ticket> findByBookingBookingId(Integer bookingBookingId);

    List<Ticket> findByStatusAndHoldExpireTimeBefore(Ticket.Status status, LocalDateTime holdExpireTimeBefore);


    @Query("SELECT COALESCE(SUM(t.ticketPrice), 0) FROM Ticket t WHERE t.status IN ('booked', 'checked_in', 'used') AND t.createdAt >= :start AND t.createdAt < :end")
    double sumRevenueBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.status IN ('booked', 'checked_in', 'used') AND t.createdAt >= :start AND t.createdAt < :end")
    int countTicketsBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);


    @Query("""
    SELECT r.routeId AS id,
           r.routeName AS name,
           COUNT(t.ticketId) AS bookings,
           SUM(t.ticketPrice) AS revenue
    FROM Ticket t
    JOIN t.trip tr
    JOIN tr.route r
    WHERE t.status IN ('booked', 'checked_in', 'used')
    GROUP BY r.routeId, r.routeName
    ORDER BY bookings DESC
""")
    List<PopularRouteProjection> findPopularRoutes();


    @Query(value = """
    SELECT 
        c.carriage_type AS name,
        COUNT(t.ticket_id) AS count
    FROM tickets t
    JOIN seats s ON t.seat_id = s.seat_id
    JOIN carriages c ON s.carriage_id = c.carriage_id
    WHERE t.ticket_status IN ('booked', 'checked_in', 'used')
    GROUP BY c.carriage_type
""", nativeQuery = true)
    List<Map<String, Object>> getTicketDistributionRaw();

    @Query(value = """
    SELECT 
        DATE_FORMAT(created_at, '%Y-%m') AS period,
        SUM(ticket_price) AS revenue,
        COUNT(*) AS totalTickets
    FROM tickets
    WHERE ticket_status IN ('booked', 'checked_in', 'used')
      AND created_at >= DATE_SUB(CURDATE(), INTERVAL 6 MONTH)
    GROUP BY period
    ORDER BY period ASC
""", nativeQuery = true)
    List<Map<String, Object>> getRevenueLast6Months();


}