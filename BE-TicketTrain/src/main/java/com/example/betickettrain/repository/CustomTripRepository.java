package com.example.betickettrain.repository;

import com.example.betickettrain.dto.TripSearchResult;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomTripRepository {

    private final EntityManager em;

    private static TripSearchResult getTripSearchResult(Object[] row) {
        TripSearchResult r = new TripSearchResult();
        try {


            // Các kiểu dữ liệu đơn giản
            r.setTripId((Integer) row[0]);
            r.setTripCode((String) row[1]);

            // Xử lý các kiểu DateTime
            if (row[2] instanceof Timestamp) {
                r.setDepartureTime(((Timestamp) row[2]).toLocalDateTime());
            } else if (row[2] instanceof LocalDateTime) {
                r.setDepartureTime((LocalDateTime) row[2]);
            }

            if (row[3] instanceof Timestamp) {
                r.setArrivalTime(((Timestamp) row[3]).toLocalDateTime());
            } else if (row[3] instanceof LocalDateTime) {
                r.setArrivalTime((LocalDateTime) row[3]);
            }

            // Các trường String đơn giản
            r.setRouteName((String) row[4]);
            r.setTrainNumber((String) row[5]);
            r.setTrainName((String) row[6]);
            r.setTrainType((String) row[7]);
            r.setOriginStation((String) row[8]);
            r.setDestinationStation((String) row[9]);

            // Xử lý trường Time thành String
            if (row[10] instanceof Time) {
                r.setDuration(row[10].toString());
            } else if (row[9] instanceof String) {
                r.setDuration((String) row[10]);
            } else if (row[10] != null) {
                r.setDuration(row[10].toString());
            }

            // Xử lý BigDecimal
            if (row[11] instanceof BigDecimal) {
                r.setMinPrice((BigDecimal) row[11]);
            } else if (row[11] instanceof Double) {
                r.setMinPrice(BigDecimal.valueOf((Double) row[11]));
            } else if (row[11] != null) {
                r.setMinPrice(new BigDecimal(row[11].toString()));
            }

            if (row[12] instanceof BigDecimal) {
                r.setMaxPrice((BigDecimal) row[12]);
            } else if (row[12] instanceof Double) {
                r.setMaxPrice(BigDecimal.valueOf((Double) row[12]));
            } else if (row[12] != null) {
                r.setMaxPrice(new BigDecimal(row[12].toString()));
            }

            // Xử lý amenities (carriage_types trong stored procedure)
            if (row[13] != null) {
                r.setAmenities(row[13].toString());
            }
// available_seats là cột thứ 13 → index 13 (bắt đầu từ 0)
            if (row.length > 14 && row[14] != null) {
                if (row[14] instanceof Integer) {
                    r.setAvailableSeats((Integer) row[14]);
                } else {
                    r.setAvailableSeats(Integer.parseInt(row[14].toString()));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return r;
    }

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
}