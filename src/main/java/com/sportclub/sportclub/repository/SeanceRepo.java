package com.sportclub.sportclub.repository;

import com.sportclub.sportclub.entities.Coach;
import com.sportclub.sportclub.entities.Seance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeanceRepo extends JpaRepository<Seance,Long> {
    Page<Seance> findByClassNameContains(String s, Pageable pageable);
}
