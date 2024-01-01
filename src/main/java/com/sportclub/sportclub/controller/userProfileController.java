package com.sportclub.sportclub.controller;


import com.sportclub.sportclub.entities.*;
import com.sportclub.sportclub.repository.CheckInRepo;
import com.sportclub.sportclub.repository.CoachCheckInRepo;
import com.sportclub.sportclub.repository.MemberAbonnementRepo;
import com.sportclub.sportclub.repository.PaymentRepo;
import com.sportclub.sportclub.service.*;
import com.sportclub.sportclub.tools.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller

public class userProfileController {

    @Autowired
    MemberAbonnementRepo memberAbonnementRepo;
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
    @Autowired
    AbonnementService abonnementService;
    @Autowired
    FileStorageService fileStorageService;

    @GetMapping("/membersList/userProfile")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('EMPLOYEE') or hasAuthority('COACH')")

    public String getMemberProfile(@RequestParam(name = "id") Long id, Model model) {

        isMembershipExpired();

        Member member = memberService.getMemberById(id);
        List<CheckIn> checkIns = checkInRepo.getCheckInByMember(member);
        List<Paiement> paiements = paymentService.getPaymentsByMember(member);
//        Abonnement abonnement = member.getCurrentAbonnement();
        List<MemberAbonnement> memberAbonnements = memberAbonnementRepo.findByMember(member, Sort.by(Sort.Direction.DESC, "id"));

        model.addAttribute("memberships", memberAbonnements);
        model.addAttribute("abos", abonnementService.getAllAbos());
        model.addAttribute("today", LocalDate.now());
        model.addAttribute("checkins", checkIns);
        model.addAttribute("payments", paiements);
        model.addAttribute("user", member);

        return "userProfile";
    }

    public boolean isMembershipExpired(){
        List<MemberAbonnement> memberAbonnementList = memberAbonnementRepo.findAll();

        LocalDate currentDate = LocalDate.now();

        for (MemberAbonnement memberAb:memberAbonnementList
             ) {
            Member member = memberAb.getMember();
            LocalDate expirationDate = null;
            String abPeriod = memberAb.getAbonnement().getPeriod();
            switch (abPeriod) {
                case "12" -> {
                    expirationDate = memberAb.getBookedDate().plusYears(1);

                }
                case "3" -> {
                    expirationDate = memberAb.getBookedDate().plusMonths(3);
                }
                case "1" -> {
                    expirationDate = memberAb.getBookedDate().plusMonths(1);
                }
                case "6" -> {
                    expirationDate = memberAb.getBookedDate().plusMonths(6);

                }
                case "2" -> {
                    expirationDate = memberAb.getBookedDate().plusMonths(2);
                }
                case "0" -> {
                }
            }
            if ((expirationDate!=null && currentDate.isAfter(expirationDate)) || (expirationDate==null && member.getNbrSessionCurrentCarnet()<= 0)){
                return true;
            }

        }
        return false;
    }
    @GetMapping("/coachProfile")
    public String getCoachProfile(@RequestParam(name = "id") Long id, Model model) {

        UserApp userApp = adminService.getAdminById(id);


        model.addAttribute("user", userApp);
        Coach coach = coachService.getCoachById(id);
        List<CheckInCoach> checkInCoaches = coachCheckInRepo.getCheckInByCoach(coach);
        model.addAttribute("checkins", checkInCoaches);
        model.addAttribute("today", LocalDate.now());

        return "coachProfile";

    }

    @GetMapping("/membersList/userProfile/updateAbo")
    public String updateMembership(@RequestParam(name = "userId") Long userId, @RequestParam(name = "abId") Long id) {
        try {
            Member member = memberService.getMemberById(userId);
            Abonnement abonnement = abonnementService.getAboById(id);

            //Add membership to the member
            MemberAbonnement memberAbonnement = new MemberAbonnement();

            memberAbonnement.setAbonnement(abonnement);
            memberAbonnement.setBookedDate(LocalDate.now());
            if (member.getMemberAbonnements() == null) {
                member.setMemberAbonnements(new ArrayList<>());
            }
            memberAbonnement.setMember(member);
            member.getMemberAbonnements().add(memberAbonnement);
            member.setNbrSessionCurrentCarnet(abonnement.getNbrSeance());

            memberService.updateMember(member);
            memberAbonnementRepo.save(memberAbonnement);
            //Add new payment
            Paiement paiement = new Paiement();
            paiement.setMember(member);
            paiement.setStart_date(LocalDate.now());
            paiement.setAbonnement(abonnement);
            paiement.setTotalAmount(abonnement.getPrice());
            paiement.setStatus("Impayé");
            String per = paiement.getAbonnement().getPeriod();
            SetPayEndDate sPD = new SetPayEndDate();
            sPD.setPayEndDate(per, paiement);
            paymentService.addPayement(paiement);

            MemberAbonnement memberMembership = memberAbonnementRepo.findByMemberAndAbonnementAndBookedDate(member,abonnement,paiement.getStart_date());


            if (memberMembership != null) {

                // Update MemberAbonnement status
                memberMembership.setAbStatus("En attente");
                memberAbonnementRepo.save(memberMembership);
                System.out.println(memberMembership);
                System.out.println("Done!!!!!!!!!");
            }

        } catch (Exception e) {
            System.out.println("Something wrong!!! " + e);
        }


        return "redirect:/membersList/userProfile?id=" + userId;
    }

}
