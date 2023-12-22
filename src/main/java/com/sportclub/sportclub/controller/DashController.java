package com.sportclub.sportclub.controller;

import com.sportclub.sportclub.entities.*;
import com.sportclub.sportclub.repository.MemberRepository;
import com.sportclub.sportclub.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class DashController {
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

    @GetMapping("/")
    @PreAuthorize("hasAuthority('ADMIN')  or hasAuthority('COACH')")

    public String getDash(Model model) {
        memberService.ifPicIsEmpty(adminService.getAllAdmins(), memberService.getAllMembers(), coachService.getAllCoachs());

        updateMemberStatues();
        List<Paiement> paiementList = paymentService.getAllPayment();
        double totalPrice = 0.0;


        for (Paiement payment : paiementList
        ) {
            if (payment != null && payment.getStatue().equals("Payé")) {
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
                    .filter(payment -> payment != null && payment.getStatue().equals("Payé")
                            && payment.getPayedAt().isEqual(selectedDate))
                    .collect(Collectors.toList());
        } else {
            paiementList = paiementList.stream()
                    .filter(payment -> payment != null && payment.getStatue().equals("Payé"))
                    .collect(Collectors.toList());
        }

        for (Paiement payment : paiementList
        ) {
            if (payment != null && payment.getStatue().equals("Payé")) {
                double price = payment.getAbonnement().getPrice();
                totalPrice += price;

            }
        }
        return ResponseEntity.ok(totalPrice);
    }

    //Update the member's statue
    public void updateMemberStatues() {
        try {

            List<Paiement> allPayments = paymentService.getAllPayment();

            for (Paiement payment : allPayments) {

                Member member = payment.getMember();
                if (member != null) {

                    boolean hasActivePayment = memberHasActivePay(member);


                    if (!hasActivePayment) {
                        member.setStatue("Inactive");
                    } else {
                        member.setStatue("Active");
                    }
                    memberService.updateMember(member);
                }

            }
        } catch (Exception e) {
            System.out.println("Something is off " + e);
        }
    }

    public boolean memberHasActivePay(Member member) {
        LocalDate currentDate = LocalDate.now();
        List<Paiement> payments = paymentService.getPaymentsByMember(member);
        int nbrSession = member.getNbrSessionCurrentCarnet();
        if (payments != null) {


            for (Paiement payment : payments) {

                if ((payment.getEnd_date() != null && currentDate.isBefore(payment.getEnd_date()) && payment.getStatue().equals("Payé")) || (nbrSession > 0 && payment.getStatue().equals("Payé"))) {
                    //If there is an active payment for the member return true
                    return true;
                }
            }
        }
            //If not
            return false;

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
