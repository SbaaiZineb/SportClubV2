package com.sportclub.sportclub.repository;

import com.sportclub.sportclub.entities.Coach;
import com.sportclub.sportclub.entities.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CoachRepository extends JpaRepository<Coach , Long> {
    Page<Coach> findByNameContains(String mc, Pageable pageable);
    List<Coach> findByNameContains(String name);
    Coach getCoachByEmail(String email);
    List<Coach> findByCinContainsIgnoreCase(String cin);
    List<Coach> findByTeleContains(String tele);

}
