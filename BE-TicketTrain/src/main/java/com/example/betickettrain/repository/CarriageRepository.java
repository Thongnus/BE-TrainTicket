package com.example.betickettrain.repository;

import com.example.betickettrain.entity.Carriage;
import com.example.betickettrain.entity.Trip;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CarriageRepository extends JpaRepository<Carriage, Integer> , JpaSpecificationExecutor<Carriage> {

    @Transactional
    @Modifying
    @Query("""
                Update Carriage  c set c.status = :status where c.carriageId = :id and c.train.trainId = :trainId
            """)
    int updateStatusOfTrain(@Param("id") Integer id, @Param("status") Carriage.Status status, @Param("trainId") Integer trainId);

    @Query("SELECT COUNT(c) FROM Carriage c Where c.status = :status")
    Integer countCarriageByStatus(String status);


    List<Carriage> findAllByTrain_TrainId(Integer trainTrainId);
}