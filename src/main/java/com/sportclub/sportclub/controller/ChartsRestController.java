package com.sportclub.sportclub.controller;

import com.sportclub.sportclub.entities.Abonnement;
import com.sportclub.sportclub.repository.MemberRepository;
import com.sportclub.sportclub.service.AbonnementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ChartsRestController {
    @Autowired
    AbonnementService abonnementService;
    @Autowired
    MemberRepository memberRepository;
    @GetMapping("/memberShipStatistic")
    public ResponseEntity<Map<String, Integer>> membership(Model model) {
        Map<String, Integer> statistics = new HashMap<>();

        // Retrieve the membership statistics from the database or calculate them as needed
        // Assuming you have a membershipService to fetch the statistics
        List<Abonnement> memberships = abonnementService.getAllAbos();
        for (Abonnement membership : memberships) {
            long userCount = memberRepository.countByAbonnement(membership);
            statistics.put(membership.getNameAb(), (int) userCount);
        }

        return ResponseEntity.ok(statistics);
    }
}
