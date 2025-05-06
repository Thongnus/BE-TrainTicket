package com.example.betickettrain.repository;

import com.example.betickettrain.entity.Cancellation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CancellationRepository extends JpaRepository<Cancellation, Integer> {
}