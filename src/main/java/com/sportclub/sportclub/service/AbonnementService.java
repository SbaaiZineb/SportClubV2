package com.sportclub.sportclub.service;

import com.sportclub.sportclub.entities.Abonnement;
import com.sportclub.sportclub.entities.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AbonnementService {

    void addAb(Abonnement abonnement);

    void deleteAbonnement(Long id);
    Page<Abonnement> findByAboName(String ac, Pageable pageable);
    Abonnement getAboById(Long  id);
    void updateAbonnement(Abonnement abonnement);
    List<Abonnement> getAllAbos();
}
