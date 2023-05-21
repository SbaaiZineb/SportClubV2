package com.sportclub.sportclub.service;

import com.sportclub.sportclub.entities.CheckIn;
import com.sportclub.sportclub.entities.Member;
import com.sportclub.sportclub.entities.Seance;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface CheckInService {
    List<CheckIn> getBySession(Long id);
    void addCheck(CheckIn checkIn);
    List<CheckIn> getAllCheckIns();
    List<CheckIn> findLatest(LocalDate date);
    List<CheckIn> getAllbyTime(LocalTime time);

}

