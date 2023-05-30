package com.sportclub.sportclub.controller;

import com.sportclub.sportclub.entities.Abonnement;
import com.sportclub.sportclub.entities.CheckIn;
import com.sportclub.sportclub.repository.MemberRepository;
import com.sportclub.sportclub.service.AbonnementService;
import com.sportclub.sportclub.service.CheckInService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.*;

@RestController
public class ChartsRestController {
    @Autowired
    AbonnementService abonnementService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    CheckInService checkInService;

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

    @GetMapping("/checkInStatistic")
    public ResponseEntity<Map<String, Integer>> checkins() {

        Map<String, Integer> statistics = new HashMap<>();

        List<CheckIn> checkIns = checkInService.getCheckInOfCurrenteek();
        for (CheckIn check:checkIns
             ) {
            DayOfWeek dayOfWeek=check.getCheckinDate().getDayOfWeek();
            String day = dayOfWeek.getDisplayName(TextStyle.FULL, Locale.FRENCH);

            // Increment the count for the specific day in the statistics map
            statistics.put(day, statistics.getOrDefault(day, 0) + 1);
        }


        return  ResponseEntity.ok(statistics);
    }
}
