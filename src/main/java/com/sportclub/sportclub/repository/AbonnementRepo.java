package com.sportclub.sportclub.repository;

import com.sportclub.sportclub.entities.Abonnement;
import com.sportclub.sportclub.entities.Coach;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AbonnementRepo extends JpaRepository<Abonnement,Long> {
    Page<Abonnement> findByNameAbContains(String ac, Pageable pageable);

    Abonnement findByNameAb(String membershipType);
}
