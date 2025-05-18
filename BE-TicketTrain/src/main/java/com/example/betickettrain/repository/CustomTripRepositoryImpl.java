package com.example.betickettrain.repository;

import com.example.betickettrain.dto.TripSearchResult;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
@Repository
@RequiredArgsConstructor
public class CustomTripRepositoryImpl implements CustomTripRepository {

    private final EntityManager em;

    @Override
    public List<TripSearchResult> searchTrips(Integer originStationId, Integer destinationStationId, LocalDate departureDate, Integer passengers) {
        List<Object[]> rows = em.createNativeQuery("CALL search_trips(:origin, :destination, :date, :passengers)")
                .setParameter("origin", originStationId)
                .setParameter("destination", destinationStationId)
                .setParameter("date", departureDate)
                .setParameter("passengers", passengers)
                .getResultList();

        List<TripSearchResult> results = new ArrayList<>();
        for (Object[] row : rows) {
            TripSearchResult r = getTripSearchResult(row);
            results.add(r);
        }
        return results;
    }

    private static TripSearchResult getTripSearchResult(Object[] row) {
        TripSearchResult r = new TripSearchResult();
        r.setTripId((Integer) row[0]);
        r.setTripCode((String) row[1]);
        r.setDepartureTime((LocalDateTime) row[2]);
        r.setArrivalTime((LocalDateTime) row[3]);
        r.setRouteName((String) row[4]);
        r.setTrainNumber((String) row[5]);
        r.setTrainType((String) row[6]);
        r.setOriginStation((String) row[7]);
        r.setDestinationStation((String) row[8]);
        r.setDuration((String) row[9]);
        r.setMinPrice((BigDecimal) row[10]);
        r.setMaxPrice((BigDecimal) row[11]);
        r.setAmenities((String) row[12]);
        return r;
    }
}
