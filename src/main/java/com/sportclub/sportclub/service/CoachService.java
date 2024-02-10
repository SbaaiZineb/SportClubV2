package com.sportclub.sportclub.service;

import com.sportclub.sportclub.entities.Abonnement;
import com.sportclub.sportclub.entities.Coach;
import com.sportclub.sportclub.entities.Member;
import org.checkerframework.checker.units.qual.C;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CoachService {
    void addCoach(Coach coach);
    Coach getCoachByEmail(String username);
    long count();
    List<Coach> getCoachBynName(String name);
    void deleteCoach(Long id);
    Page<Coach> findByCoachName(String mc, Pageable pageable);
    Coach getCoachById(Long id);
    void updateCoach(Coach c);
    List<Coach> getAllCoachs();
    List<Coach> getCoachByCin(String cin);
    List<Coach> getCoachByTele(String tele);
    List<Coach> findByKeyword(String keyword);
}
