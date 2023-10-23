package com.sportclub.sportclub.controller;

import com.sportclub.sportclub.entities.Abonnement;
import com.sportclub.sportclub.entities.CheckIn;
import com.sportclub.sportclub.entities.Member;
import com.sportclub.sportclub.entities.Paiement;
import com.sportclub.sportclub.repository.MemberRepository;
import com.sportclub.sportclub.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
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
    @Autowired
    CheckInService checkInService;
    @GetMapping("/")
    public String getDash(Model model){

        List<Paiement> paiementList=paymentService.getAllPayment();
        double totalPrice = 0.0;
        for (Paiement payment:paiementList
             ) {
            if ( payment!= null && payment.getStatue().equals("Payé")){
                double price=payment.getAbonnement().getPrice();
                totalPrice +=price;

            }
        }
        model.addAttribute("price",totalPrice);
        System.out.println(totalPrice);
        long countM=memberService.count();
        List<CheckIn> checkIns = checkInService.getCheckInOfWeek();
        long size=checkIns.size();
        long countC=coachService.count();
        model.addAttribute("today", LocalDate.now());
        model.addAttribute("size",size);
        model.addAttribute("countC",countC);
        model.addAttribute("countM",countM);

        List<Member> members = memberService.getAllMembers();
        model.addAttribute("members",members);

        return "index";
    }
    @Scheduled(fixedRate = 2000)
    //Update the member's statue
    public void updateMemberStatues() {
        List<Paiement> allPayments = paymentService.getAllPayment();
        LocalDate currentDate = LocalDate.now();

        for (Paiement payment : allPayments) {
            LocalDate payEndDate = payment.getEnd_date();
            Member member = payment.getMember();
            boolean hasActivePayment = memberHasActivePay(member, allPayments);

            if (!hasActivePayment && currentDate.isAfter(payEndDate)) {
                member.setStatue("Inactive");
            } else if (hasActivePayment && currentDate.isBefore(payEndDate)) {
                member.setStatue("Active");
            }
            memberService.updateMember(member);
        }
    }
    public boolean memberHasActivePay(Member member, List<Paiement> payments) {
        LocalDate currentDate = LocalDate.now();
        for (Paiement payment : payments) {

            if (payment.getMember().equals(member) && currentDate.isBefore(payment.getEnd_date())) {
                //If there is an active payment for the member return true
                return true;
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
