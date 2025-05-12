package com.example.betickettrain.repository;

import com.example.betickettrain.entity.Newfeed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewfeedRepository extends JpaRepository<Newfeed, Long> {
    // Custom query methods can be defined here if needed
    // For example, you can add methods to find Newfeed by title or other attributes
    // Example: List<Newfeed> findByTitleContaining(String title);

}