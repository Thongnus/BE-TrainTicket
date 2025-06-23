package com.example.betickettrain.repository;

import com.example.betickettrain.entity.Train;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrainRepository extends JpaRepository<Train, Long> {
    @Query("SELECT COUNT(t) FROM Train t")
    int countAll();

    @Modifying
    @Query("""
            update Train t set t.status = :status where t.trainId = :id
            """)
    int updateStatusOfTrain(@Param("id") Long id, @Param("status") String status);

    List<Train> findAllByStatus(Train.Status status);
    // Add custom query methods if needed


}