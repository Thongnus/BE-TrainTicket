package com.example.betickettrain.repository;

import com.example.betickettrain.dto.PopularTripDto;
import com.example.betickettrain.dto.TrainRouteProjection;
import com.example.betickettrain.entity.Trip;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface TripRepository extends JpaRepository<Trip, Integer>, JpaSpecificationExecutor<Trip> {
    boolean existsByTripCode(String tripCode);

    @Query(nativeQuery = true, value = "select * from Trips t where DATE(departure_time) = :date and t.status")
    Trip findTripByDepartureTimeAndStatus(LocalDateTime departureTime, String status);

    @Procedure(procedureName = "find_trip_by_trip_code")
    Trip findTripByTripCode(String tripCode);

    @Query("SELECT t FROM Trip t " +
            "JOIN FETCH t.route r " +
            "JOIN FETCH r.originStation " +
            "JOIN FETCH r.destinationStation " +
            "JOIN FETCH t.train " +
            "WHERE t.tripId = :tripId")
    Optional<Trip> findByIdWithRouteAndStations(@Param("tripId") Integer tripId);

    @Query(nativeQuery = true, value = "SELECT " +
            "t.trip_id AS tripId, " +
            "t.trip_code AS tripCode, " +
            "r.route_name AS routeName, " +
            "os.station_name AS origin_station, " +
            "ds.station_name AS destination_station, " +
            "COUNT(tk.ticket_id) AS total_tickets, " +
            "AVG(f.rating) AS average_rating " +
            "FROM trips t " +
            "JOIN routes r ON t.route_id = r.route_id " +
            "JOIN stations os ON r.origin_station_id = os.station_id " +
            "JOIN stations ds ON r.destination_station_id = ds.station_id " +
            "JOIN tickets tk ON t.trip_id = tk.trip_id " +
            "LEFT JOIN feedbacks f ON t.trip_id = f.trip_id " +
            "WHERE tk.ticket_status IN ('booked', 'checked_in', 'used') " +
            "GROUP BY t.trip_id " +
            "ORDER BY total_tickets DESC, average_rating DESC " +
            "LIMIT 12")
    List<PopularTripDto> getTripPopular();

    @Query(nativeQuery = true, value = """
            
                        SELECT\s
                t.trip_id AS tripId,
                 tk.origin_station_id AS originStation,
                tk.destination_station_id AS  destinationStation,
                s1.station_name AS departure,
                s2.station_name AS arrival,
                t.departure_time AS departureTime,
                t.arrival_time AS arrivalTime,
                tr.train_number AS trainNumber,
                AVG(tk.ticket_price) AS averagePrice,
                COUNT(tk.ticket_id) AS ticketCount
                        FROM trips t
                        JOIN tickets tk ON tk.trip_id = t.trip_id
                        JOIN trains tr ON t.train_id = tr.train_id
                        JOIN stations s1 ON tk.origin_station_id = s1.station_id
                        JOIN stations s2 ON tk.destination_station_id = s2.station_id
                        WHERE tk.ticket_status IN ('booked', 'checked_in', 'used')
                        GROUP BY t.trip_id, s1.station_name, s2.station_name, t.departure_time, t.arrival_time, tr.train_number,tk.origin_station_id ,  tk.destination_station_id
                        HAVING COUNT(tk.ticket_id) > 0
                        ORDER BY ticketCount DESC
                        LIMIT :limit;
            
            """)
    List<TrainRouteProjection> findPopularRoutes(@Param("limit") Integer limit);
    @Query("SELECT COUNT(t) FROM Trip t WHERE t.status = 'completed'")
    int countCompletedTrips();

    @Query("SELECT COUNT(t) FROM Trip t WHERE t.status = 'completed' AND t.departureTime BETWEEN :start AND :end")
    int counttCompletedTripsBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);


    //Update trips to completed if they are scheduled and expired
    @Modifying
    @Query(value = """
    UPDATE trips 
    SET status = 'completed' 
    WHERE status = 'scheduled' 
      AND arrival_time < :expiredBefore
""", nativeQuery = true)
    int markTripsCompletedIfExpired(@Param("expiredBefore") LocalDateTime expiredBefore);

    @Query(value = """
    SELECT DISTINCT
        COALESCE(NULLIF(TRIM(b.contact_email), ''), u.email) AS email
    FROM tickets t
    JOIN bookings b ON t.booking_id = b.booking_id
    JOIN users u ON b.user_id = u.user_id
    WHERE t.trip_id = :tripId
      AND b.booking_status IN ('confirmed', 'completed')
      AND t.ticket_status IN ('booked', 'checked_in')
      AND (
          b.contact_email IS NOT NULL AND TRIM(b.contact_email) != ''
          OR u.email IS NOT NULL
      )
""", nativeQuery = true)
    List<String> findEffectiveEmailsByTripId(@Param("tripId") Integer tripId);
    @Query("SELECT  t.tripCode FROM Trip t")
    Set<String> findAllTripCodes();
}