package com.sportclub.sportclub.service;

import com.sportclub.sportclub.entities.Coach;
import com.sportclub.sportclub.entities.Seance;
import com.sportclub.sportclub.repository.SeanceRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class SeanceServiceImp implements SeanceService {
    @Autowired
    SeanceRepo seanceRepo;

    @Override
    public void addSeance(Seance seance) {
        seanceRepo.save(seance);
    }


    @Override
    public void deletSeance(Long id) {
        seanceRepo.deleteById(id);
    }

    @Override
    public Page<Seance> findBySeanceName(String mc, Pageable pageable) {
        return seanceRepo.findByClassNameContains(mc, pageable);
    }


    @Override
    public Seance getSeanceById(Long id) {
        return seanceRepo.findById(id).get();
    }

    @Override
    public void updateSeance(Seance s) {
        seanceRepo.save(s);
    }
    @Override
    public List<Seance> getAllSeance() {
        return seanceRepo.findAll();
    }

}
