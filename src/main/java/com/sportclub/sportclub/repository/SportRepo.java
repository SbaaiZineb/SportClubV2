package com.sportclub.sportclub.repository;

import com.sportclub.sportclub.entities.Abonnement;
import com.sportclub.sportclub.entities.Sport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SportRepo extends JpaRepository<Sport,Long> {
    Page<Sport> findByNomContains(String ac, Pageable pageable);

    List<Sport> findByNom(String nom);
    List<Sport> findByNomContains(String name);
}
