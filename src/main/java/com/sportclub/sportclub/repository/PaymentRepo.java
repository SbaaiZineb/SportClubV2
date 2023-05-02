package com.sportclub.sportclub.repository;

import com.sportclub.sportclub.entities.Paiement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepo extends JpaRepository<Paiement,Long> {
}
