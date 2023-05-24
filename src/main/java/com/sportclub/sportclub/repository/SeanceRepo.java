package com.sportclub.sportclub.repository;

import com.sportclub.sportclub.entities.Abonnement;
import com.sportclub.sportclub.entities.Coach;
import com.sportclub.sportclub.entities.Member;
import com.sportclub.sportclub.entities.Seance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface SeanceRepo extends JpaRepository<Seance,Long> {
    Page<Seance> findByClassNameContains(String s, Pageable pageable);
    List<Seance> findByClassNameContains(String s);
    List<Seance> findByCoach(Coach coach);
    List<Seance> findByStartDate(LocalDate date);
}
