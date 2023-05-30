package com.sportclub.sportclub.service;

import com.sportclub.sportclub.entities.Sport;
import com.sportclub.sportclub.repository.SportRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SportServiceImp implements SportService {
    @Autowired
    SportRepo sportRepo;

    @Override
    public void addSport(Sport sport) {
        sportRepo.save(sport);
    }

    @Override
    public void deleteSport(Long id) {
        deleteSport(id);
    }

    @Override
    public Page<Sport> findByName(String ac, Pageable pageable) {
        return sportRepo.findByNomContains(ac, pageable);
    }

    @Override
    public Sport getSportById(Long id) {
        return sportRepo.findById(id).get();
    }

    @Override
    public void updateSport(Sport sport) {
        sportRepo.save(sport);
    }

    @Override
    public List<Sport> getAllSports() {
        return sportRepo.findAll();
    }

    @Override
    public List<Sport> findByNameCon(String kw) {
        return sportRepo.findByNom(kw);
    }
}
