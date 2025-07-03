package com.example.betickettrain.repository;

import com.example.betickettrain.entity.RefundPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RefundPolicyRepository extends JpaRepository<RefundPolicy, Integer> {
  List<RefundPolicy> findAllByOrderByHoursBeforeDepartureDesc();

}