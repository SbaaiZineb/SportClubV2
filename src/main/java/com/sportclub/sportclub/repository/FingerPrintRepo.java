package com.sportclub.sportclub.repository;

import com.sportclub.sportclub.entities.Finger;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface  FingerPrintRepo extends JpaRepository<Finger, Long> {
    List<Finger> findByfPrint(byte[] template);
}
