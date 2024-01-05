package com.sportclub.sportclub.controller;

import com.sportclub.sportclub.entities.*;
import com.sportclub.sportclub.repository.MemberAbonnementRepo;
import com.sportclub.sportclub.repository.MemberRepository;
import com.sportclub.sportclub.repository.PaymentRepo;
import com.sportclub.sportclub.service.AbonnementService;
import com.sportclub.sportclub.service.CheckInService;
import com.sportclub.sportclub.service.CoachCheckInService;
import com.sportclub.sportclub.service.CoachService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;

@RestController
public class ChartsRestController {
    @Autowired
    AbonnementService abonnementService;
    @Autowired
    MemberAbonnementRepo memberAbonnementRepo;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    CheckInService checkInService;
    @Autowired
    CoachCheckInService coachCheckInService;
    @Autowired
    CoachService coachService;
    @Autowired
    PaymentRepo paymentRepo;

    @GetMapping("/memberShipStatistic")
    public ResponseEntity<Map<String, Integer>> membership(Model model) {
        Map<String, Integer> statistics = new HashMap<>();

        // Retrieve the membership statistics from the database or calculate them as needed
        // Assuming you have a membershipService to fetch the statistics
        List<Abonnement> memberships = abonnementService.getAllAbos();
        for (Abonnement membership : memberships) {
            long userCount = memberRepository.countByMemberAbonnements_Abonnement(membership);
            statistics.put(membership.getNameAb(), (int) userCount);
        }

        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/checkInStatistic")
    public ResponseEntity<Map<String, Integer>> checkins(@RequestParam("selectedDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate selectedDate) {

        Map<String, Integer> statistics = new HashMap<>();

        List<CheckIn> checkIns = checkInService.getCheckInOfCurrenteek(selectedDate);
        for (CheckIn check : checkIns
        ) {
            DayOfWeek dayOfWeek = check.getCheckinDate().getDayOfWeek();
            String day = dayOfWeek.getDisplayName(TextStyle.FULL, Locale.FRENCH);

            // Increment the count for the specific day in the statistics map
            statistics.put(day, statistics.getOrDefault(day, 0) + 1);
        }


        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/checkInMember")
    public ResponseEntity<Map<String, Integer>> checkinMember(@RequestParam("selectedDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate selectedDate, @RequestParam("id") Long id) {

        Map<String, Integer> statistics = new HashMap<>();
        Member member = memberRepository.findById(id).get();
        List<CheckIn> checkIns = checkInService.getByMember(member, selectedDate);
        for (CheckIn check : checkIns
        ) {
            Month month = check.getCheckinDate().getMonth();
            String monthName = month.getDisplayName(TextStyle.FULL, Locale.FRENCH);

// Increment the count for the specific month in the statistics map
            statistics.put(monthName, statistics.getOrDefault(monthName, 0) + 1);
        }

        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/coachStatistic")
    public ResponseEntity<Map<String, Integer>> coachCheck(@RequestParam("dateSelected") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateSelected, @RequestParam("id") Long id) {

        Map<String, Integer> statistics = new HashMap<>();
        Coach coach = coachService.getCoachById(id);
        List<CheckInCoach> checkIns = coachCheckInService.getCheckInOfCurrentWeek(coach, dateSelected);
        for (CheckInCoach check : checkIns
        ) {
            DayOfWeek dayOfWeek = check.getCheckinDate().getDayOfWeek();
            String day = dayOfWeek.getDisplayName(TextStyle.FULL, Locale.FRENCH);

            // Increment the count for the specific day in the statistics map
            statistics.put(day, statistics.getOrDefault(day, 0) + 1);
        }


        return ResponseEntity.ok(statistics);
    }

    // Subscription statistics
    @GetMapping("/subStatistics")
    public ResponseEntity<Map<String, Integer>> getSubscriptionStatisticsForYear(@RequestParam(name = "year") int year) {
        Map<String, Integer> statistics = new HashMap<>();
        List<MemberAbonnement> abonnements = new ArrayList<>();

        // Retrieve subscriptions for the entire year
        List<MemberAbonnement> subscriptions = memberAbonnementRepo.findByBookedDateYear(year);
        for (MemberAbonnement subsciption:subscriptions
             ) {
            if (!subsciption.getAbStatus().equals("Annulé")){
                abonnements.add(subsciption);
            }

        }


        for (MemberAbonnement memberAbonnement : abonnements) {
            LocalDate bookedDate = memberAbonnement.getBookedDate();
            Month month = bookedDate.getMonth();


            String monthName = month.getDisplayName(TextStyle.SHORT, Locale.FRENCH);

            // Increment the count for the specific month in the statistics map
            statistics.put(monthName, statistics.getOrDefault(monthName, 0) + 1);

        }

        return ResponseEntity.ok(statistics);
    }
@GetMapping("/paymentStatistics")
public ResponseEntity<Map<String, Integer>> getPaymentStatistic(@RequestParam(name = "year") int year) {
    Map<String, Integer> statistics = new HashMap<>();

    // Retrieve payment for the entire year
    List<Paiement> paiements = paymentRepo.findByPayedAtYear(year);


    for (Paiement paiement : paiements) {
        LocalDate payedAt = paiement.getPayedAt();
        Month month = payedAt.getMonth();
        double montant = paiement.getMontant();


        String monthName = month.getDisplayName(TextStyle.SHORT, Locale.FRENCH);

        // Increment the count for the specific month in the statistics map
        int currentRevenue = statistics.getOrDefault(monthName, 0);
        statistics.put(monthName, currentRevenue + (int) montant);    }

    return ResponseEntity.ok(statistics);
}

}
