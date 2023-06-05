package com.sportclub.sportclub.controller;

import com.sportclub.sportclub.entities.*;
import com.sportclub.sportclub.repository.CheckInRepo;
import com.sportclub.sportclub.repository.CoachCheckInRepo;
import com.sportclub.sportclub.repository.PaymentRepo;
import com.sportclub.sportclub.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
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
    @Autowired
    AdminService adminService;
    @Autowired
    CoachCheckInRepo coachCheckInRepo;
    @Autowired
    CoachService coachService;
    @GetMapping("/userProfile")
    public String getMemberProfile(@RequestParam(name = "id") Long id, Model model){
        UserApp userApp=adminService.getAdminById(id);
        if (userApp.getRoles().getRoleName().equals("COACH")){
            model.addAttribute("user",userApp);
            Coach coach=coachService.getCoachById(id);
            List<CheckInCoach> checkInCoaches=coachCheckInRepo.getCheckInByCoach(coach);
            model.addAttribute("checkins",checkInCoaches);

        }else{
        Member member=memberService.getMemberById(id);
        List<CheckIn> checkIns=checkInRepo.getCheckInByMember(member);
        List<Paiement> paiements=paymentRepo.findPaiementByMember(member);

        model.addAttribute("today", LocalDate.now());
        model.addAttribute("checkins",checkIns);
        model.addAttribute("payments",paiements);
        model.addAttribute("user",member);}
        model.addAttribute("today",LocalDate.now());
        return "userProfile";
    }
}
