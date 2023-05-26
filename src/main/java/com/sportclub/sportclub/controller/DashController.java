package com.sportclub.sportclub.controller;

import com.sportclub.sportclub.entities.Abonnement;
import com.sportclub.sportclub.entities.Paiement;
import com.sportclub.sportclub.repository.MemberRepository;
import com.sportclub.sportclub.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

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
    @GetMapping("/")
    public String getDash(Model model){

        List<Paiement> paiementList=paymentService.getAllPayment();
        double totalPrice = 0.0;
        for (Paiement payment:paiementList
             ) {
            if (payment.getStatue().equals("Payé")){
                double price=payment.getAbonnement().getPrice();
                totalPrice +=price;

            }
        }
        model.addAttribute("price",totalPrice);
        System.out.println(totalPrice);
        long countM=memberService.count();
        long countC=coachService.count();
        model.addAttribute("countC",countC);
        model.addAttribute("countM",countM);
        return "index";
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
