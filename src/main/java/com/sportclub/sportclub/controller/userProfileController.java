package com.sportclub.sportclub.controller;

import com.sportclub.sportclub.entities.CheckIn;
import com.sportclub.sportclub.entities.Member;
import com.sportclub.sportclub.entities.Paiement;
import com.sportclub.sportclub.repository.CheckInRepo;
import com.sportclub.sportclub.repository.PaymentRepo;
import com.sportclub.sportclub.service.CheckInService;
import com.sportclub.sportclub.service.MemberService;
import com.sportclub.sportclub.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class userProfileController {
    @Autowired
    MemberService memberService;
    @Autowired
    PaymentService paymentService;
    @Autowired
    CheckInRepo checkInRepo;
    @Autowired
    PaymentRepo paymentRepo;
    @GetMapping("/userProfile")
    public String getMemberProfile(@RequestParam(name = "id") Long id, Model model){

        Member member=memberService.getMemberById(id);
        List<CheckIn> checkIns=checkInRepo.getCheckInByMember(member);
        List<Paiement> paiements=paymentRepo.findPaiementByMember(member);

        model.addAttribute("checkins",checkIns);
        model.addAttribute("payments",paiements);
        model.addAttribute("member",member);
        return "userProfile";
    }
}
