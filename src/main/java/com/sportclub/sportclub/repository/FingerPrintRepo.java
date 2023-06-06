package com.sportclub.sportclub.repository;

import com.sportclub.sportclub.entities.Finger;
import org.springframework.data.jpa.repository.JpaRepository;

public interface  FingerPrintRepo extends JpaRepository<Finger, Long> {
}
