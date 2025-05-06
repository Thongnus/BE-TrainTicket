package com.example.betickettrain.repository;

import com.example.betickettrain.entity.Carriage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarriageRepository extends JpaRepository<Carriage, Integer> {
}