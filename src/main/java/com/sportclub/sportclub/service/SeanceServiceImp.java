package com.sportclub.sportclub.service;

import com.sportclub.sportclub.entities.Abonnement;
import com.sportclub.sportclub.entities.Coach;
import com.sportclub.sportclub.entities.Member;
import com.sportclub.sportclub.entities.Seance;
import com.sportclub.sportclub.repository.CoachRepository;
import com.sportclub.sportclub.repository.SeanceRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;


@Service
public class SeanceServiceImp implements SeanceService {
    @Autowired
    SeanceRepo seanceRepo;
    @Autowired
    CoachRepository coachRepository;
    @Override
    public List<Seance> getSeanceByCoach(Long abId) {
        Coach coach = coachRepository.findById(abId).get();
        return seanceRepo.findByCoach(coach);
    }

    @Override
    public List<Seance> getSeanceByStartDate(LocalDate date) {

        return seanceRepo.findByStartDate(date);
    }

    @Override
    public void addSeance(Seance seance) {
        seanceRepo.save(seance);
    }

    @Override
    public List<Seance> getSeanceBynName(String name) {
        return seanceRepo.findByClassNameContainsIgnoreCase(name);
    }


    @Override
    public void deletSeance(Long id) {
        seanceRepo.deleteById(id);
    }

    @Override
    public Page<Seance> findBySeanceName(String mc, Pageable pageable) {
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.DESC, "id"));

        return seanceRepo.findByClassNameContains(mc, pageable);
    }

    @Override
    public Page<Seance> findSeanceByCoachEmail(String username, Pageable pageable) {
        return seanceRepo.findSeanceByCoachEmail(username,pageable);
    }

    @Override
    public List<Seance> getBYCoachEmail(String username) {
        return seanceRepo.findSeanceByCoachEmail(username);
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
