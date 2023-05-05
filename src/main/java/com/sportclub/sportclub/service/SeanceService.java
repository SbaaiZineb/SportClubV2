package com.sportclub.sportclub.service;

import com.sportclub.sportclub.entities.Abonnement;
import com.sportclub.sportclub.entities.Member;
import com.sportclub.sportclub.entities.Seance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SeanceService {
    void addSeance(Seance seance);
    List<Seance> getSeanceBynName(String name);
    public List<Seance> getAllSeance();
    void deletSeance(Long id);
    Page<Seance> findBySeanceName(String mc, Pageable pageable);
    Seance getSeanceById(Long id);
    void updateSeance(Seance s);
}
