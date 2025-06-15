package com.example.backend.repository;

import com.example.backend.model.Wine;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface WineRepository extends JpaRepository<Wine, Long>, JpaSpecificationExecutor<Wine> {
    Page<Wine> findAllByDateAddedAfter(LocalDateTime since, Pageable pageable);
}
