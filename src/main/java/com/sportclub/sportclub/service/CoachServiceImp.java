package com.sportclub.sportclub.service;

import com.sportclub.sportclub.entities.Coach;
import com.sportclub.sportclub.repository.CoachRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CoachServiceImp implements CoachService {
    @Autowired
    CoachRepository coachRepository;
    public long count() {
        return coachRepository.count();
    }
    @Override
    public void addCoach(Coach coach) {
        coachRepository.save(coach);
    }

    @Override
    public Coach getCoachByEmail(String username) {
        return coachRepository.getCoachByEmail(username);
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
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.DESC, "id"));

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

    @Override
    public List<Coach> getCoachByCin(String cin) {
        return coachRepository.findByCinContainsIgnoreCase(cin);
    }

    @Override
    public List<Coach> getCoachByTele(String tele) {
        return coachRepository.findByTeleContains(tele);
    }

    @Override
    public List<Coach> findByKeyword(String keyword) {
        return coachRepository.findByKeyword(keyword);
    }
}
