
package com.example.betickettrain.repository;

import com.example.betickettrain.entity.Train;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainRepository extends JpaRepository<Train, Long> {
    // Add custom query methods if needed
}