package com.example.betickettrain.repository;

import com.example.betickettrain.entity.TripSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TripScheduleRepository extends JpaRepository<TripSchedule, Integer> {
    List<TripSchedule> findByTripTripIdOrderByScheduledArrivalAsc(Integer tripId);
@Query(value = """
select tc from TripSchedule tc inner join Trip t on t.tripId = tc.trip.tripId inner join Station st on st.stationId = tc.station.stationId where tc.trip.tripId= :tripId and 
      st.stationId = :stationId
""")
Optional<TripSchedule> findByTrip(Integer tripId, Integer stationId);

    Optional<TripSchedule> findByTripTripIdAndStationStationId(Integer tripId, Integer stationId);
}