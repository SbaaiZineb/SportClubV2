package com.sportclub.sportclub.service;

import com.sportclub.sportclub.entities.Abonnement;
import com.sportclub.sportclub.repository.AbonnementRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AbonnementServiceImp implements AbonnementService {
    @Autowired
    AbonnementRepo abonnementRepo;

    @Override
    public void addAb(Abonnement abonnement) {
        abonnementRepo.save(abonnement);
    }

    @Override
    public void deleteAbonnement(Long id) {
        abonnementRepo.deleteById(id);
    }

    @Override
    public Page<Abonnement> findByAboName(String ac, Pageable pageable) {
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.ASC, "id"));

        return abonnementRepo.findByNameAbContains(ac, pageable);
    }

    @Override
    public Abonnement getAboById(Long id) {
        return abonnementRepo.findById(id).get();
    }

    @Override
    public void updateAbonnement(Abonnement abonnement) {
        abonnementRepo.save(abonnement);
    }

    @Override
    public List<Abonnement> getAllAbos() {
        return abonnementRepo.findAll();
    }

    @Override
    public List<Abonnement> getByAbName(String keyword) {
        return abonnementRepo.findByNameAbContainsIgnoreCase(keyword);
    }
}
