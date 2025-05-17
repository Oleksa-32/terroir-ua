package com.example.backend.repository;

import com.example.backend.model.Wine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WineRepository extends JpaRepository<Wine, Long> {
}
