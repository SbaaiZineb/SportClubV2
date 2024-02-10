package com.sportclub.sportclub.controller;

import com.sportclub.sportclub.entities.*;
import com.sportclub.sportclub.repository.MemberAbonnementRepo;
import com.sportclub.sportclub.repository.MemberRepository;
import com.sportclub.sportclub.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class DashController {
    @Autowired
    MemberAbonnementRepo memberAbonnementRepo;
    @Autowired
    MemberService memberService;
    @Autowired
    AbonnementService abonnementService;
    @Autowired
    SeanceService seanceService;
    @Autowired
    CoachService coachService;
    @Autowired
    PaymentService paymentService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    GymService gymService;
    @Autowired
    CheckInService checkInService;
    @Autowired
    AdminService adminService;

    @RequestMapping("/404")
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handle404Error() {
        return "404";
    }
    @GetMapping("/")
    @PreAuthorize("hasAuthority('ADMIN')  or hasAuthority('COACH')")

    public String getDash(Model model) {
        LocalDate currentDate = LocalDate.now();
        memberService.ifPicIsEmpty(adminService.getAllAdmins(), memberService.getAllMembers(), coachService.getAllCoachs());

        //Membership status check and update
        List<MemberAbonnement> memberAbonnementList = memberAbonnementRepo.findAll();

        for (MemberAbonnement memberAb : memberAbonnementList
        ) {
            if (memberAb != null) {

                LocalDate startDate = memberAb.getStartDate();
                LocalDate endDate = memberAb.getEndDate();

                boolean isWithinValidPeriod = (endDate != null) && (
                        (startDate.isBefore(currentDate) || startDate.isEqual(currentDate)) &&
                                (endDate.isAfter(currentDate) || endDate.isEqual(currentDate)));

                boolean isUnlimitedMembership = (endDate == null) &&
                        (!currentDate.isBefore(startDate)) &&
                                memberAb.getNbrSessionCarnet() >= 0;

                if (!memberAb.getAbStatus().equals("Annulé")) {
                    if (memberService.isMembershipExpired(memberAb)) {
                        memberAb.setAbStatus("Expiré");

                    } else if (memberAb.getAbStatus().equals("Programmé") && (isWithinValidPeriod || isUnlimitedMembership)) {
                        System.out.println("Membership ------------>>> "+memberAb);
                        memberAb.setAbStatus("Active");
                    }
                }
            }
        }
        memberAbonnementRepo.saveAll(memberAbonnementList);
        memberService.updateMemberStatues();

        List<Paiement> paiementList = paymentService.getAllPayment();
        double totalPrice = 0.0;


        for (Paiement payment : paiementList
        ) {
            if (payment != null && payment.getStatus().equals("Payé")) {
                double price = payment.getAbonnement().getPrice();
                totalPrice += price;

            }
        }
        model.addAttribute("price", totalPrice);
        System.out.println(totalPrice);
        long countM = memberService.count();
        List<CheckIn> checkIns = checkInService.getCheckInOfWeek();
        long size = checkIns.size();
        long countC = coachService.count();


        model.addAttribute("today", LocalDate.now());
        model.addAttribute("size", size);
        model.addAttribute("countC", countC);
        model.addAttribute("countM", countM);

        List<Member> members = memberService.getAllMembers();
        model.addAttribute("members", members);
        model.addAttribute("gymName", gymService.getById(1L).getName());
        return "index";
    }


    @GetMapping("/totalPrice")
    public ResponseEntity<Double> getTotalPrice(@RequestParam(name = "selectedDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate selectedDate) {
        List<Paiement> paiementList = paymentService.getAllPayment();
        double totalPrice = 0.0;

        // Filter payments based on the selected date (if provided)
        if (selectedDate != null) {
            paiementList = paiementList.stream()
                    .filter(payment -> payment != null && payment.getStatus().equals("Payé")
                            && payment.getPayedAt().isEqual(selectedDate))
                    .collect(Collectors.toList());
        } else {
            paiementList = paiementList.stream()
                    .filter(payment -> payment != null && payment.getStatus().equals("Payé"))
                    .collect(Collectors.toList());
        }

        for (Paiement payment : paiementList
        ) {
            if (payment != null && payment.getStatus().equals("Payé")) {
                double price = payment.getAbonnement().getPrice();
                totalPrice += price;

            }
        }
        return ResponseEntity.ok(totalPrice);
    }



/*@GetMapping("/memberShipStatistic")
    public String membership(Model model){
    List<Object[]> userStats = memberRepository.countUsersByMembership();

    List<String> membershipNames = new ArrayList<>();
    List<Integer> userCounts = new ArrayList<>();

    for (Object[] stat : userStats) {
        Abonnement membership = (Abonnement) stat[0];
        Long count = (Long) stat[1];

        membershipNames.add(membership.getNameAb());
        userCounts.add(count.intValue());
    }

    model.addAttribute("membershipNames", membershipNames);
    model.addAttribute("userCounts", userCounts);
        return "index";
    }*/


}
