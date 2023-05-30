package com.sportclub.sportclub.service;

import com.sportclub.sportclub.entities.Abonnement;
import com.sportclub.sportclub.entities.Sport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SportService {
    void addSport(Sport sport);

    void deleteSport(Long id);
    Page<Sport> findByName(String ac, Pageable pageable);
    Sport getSportById(Long  id);
    void updateSport(Sport sport);
    List<Sport> getAllSports();
    List<Sport> findByNameCon(String kw);
}
