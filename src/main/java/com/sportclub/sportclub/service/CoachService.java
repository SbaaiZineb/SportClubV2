package com.sportclub.sportclub.service;

import com.sportclub.sportclub.entities.Abonnement;
import com.sportclub.sportclub.entities.Coach;
import com.sportclub.sportclub.entities.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CoachService {
    void addCoach(Coach coach);

    List<Coach> getCoachBynName(String name);
    void deleteCoach(Long id);
    Page<Coach> findByCoachName(String mc, Pageable pageable);
    Coach getCoachById(Long id);
    void updateCoach(Coach c);
    List<Coach> getAllCoachs();
}
