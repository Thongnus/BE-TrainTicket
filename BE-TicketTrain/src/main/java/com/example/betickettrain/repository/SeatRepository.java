package com.example.betickettrain.repository;

import com.example.betickettrain.entity.Seat;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Integer> {

    boolean existsBySeatNumber(String seatNumber);
    Seat findBySeatNumber(String seatNumber);
    Seat findBySeatId(Integer seatId);
    @Modifying
    @Query("""
    UPDATE Seat s 
    SET s.status = :status 
    WHERE s.seatId IN :seatIds 
      AND s.carriage.carriageId = :carriageId 
      AND s.carriage.train.trainId = :trainId
""")
    int updateSeatStatusByIdsAndCarriageAndTrain(@Param("seatIds") List<Integer> seatIds,
                                                 @Param("carriageId") Integer carriageId,
                                                 @Param("trainId") Integer trainId,
                                                 @Param("status") Seat.Status status);


    List<Seat> findByCarriageCarriageId(Integer carriageCarriageId);
}