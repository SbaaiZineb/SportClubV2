package com.sportclub.sportclub.service;

import com.sportclub.sportclub.entities.Coach;
import com.sportclub.sportclub.repository.CoachRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CoachServiceImp implements CoachService {
    @Autowired
    CoachRepository coachRepository;

    @Override
    public void addCoach(Coach coach) {
        coachRepository.save(coach);
    }

    @Override
    public List<Coach> getCoachBynName(String name) {
        return null;
    }

    @Override
    public void deleteCoach(Long id) {
        coachRepository.deleteById(id);
    }

    @Override
    public Page<Coach> findByCoachName(String name, Pageable pageable) {
        return coachRepository.findByNameContains(name,pageable);
    }

    @Override
    public Coach getCoachById(Long id) {
        return coachRepository.findById(id).get();
    }

    @Override
    public void updateCoach(Coach c) {
        coachRepository.save(c);
    }

    @Override
    public List<Coach> getAllCoachs() {
        return coachRepository.findAll();
    }
}
