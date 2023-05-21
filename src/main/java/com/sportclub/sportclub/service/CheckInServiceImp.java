package com.sportclub.sportclub.service;

import com.sportclub.sportclub.entities.CheckIn;
import com.sportclub.sportclub.entities.Seance;
import com.sportclub.sportclub.repository.CheckInRepo;
import com.sportclub.sportclub.repository.SeanceRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class CheckInServiceImp implements CheckInService{
    @Autowired
    CheckInRepo checkInRepo;
    @Autowired
    SeanceRepo seanceRepo;
    @Override
    public List<CheckIn> getBySession(Long id) {
        Seance seance=seanceRepo.findById(id).get();
        return checkInRepo.findBySession(seance);
    }

    @Override
    public void addCheck(CheckIn checkIn) {
        checkInRepo.save(checkIn);
    }

    @Override
    public List<CheckIn> getAllCheckIns() {
        return checkInRepo.findAll();
    }
    @Override
    public List<CheckIn> getAllbyTime(LocalTime time) {
        return null;
    }
    @Override
    public List<CheckIn> findLatest(LocalDate date) {
        return checkInRepo.getCheckInByCheckinDate(date);
    }
}
