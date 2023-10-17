package com.sportclub.sportclub.controller;

import com.sportclub.sportclub.entities.CheckInCoach;
import com.sportclub.sportclub.entities.Coach;
import com.sportclub.sportclub.entities.UserApp;
import com.sportclub.sportclub.repository.CoachCheckInRepo;
import com.sportclub.sportclub.service.AdminService;
import com.sportclub.sportclub.service.CoachCheckInService;
import com.sportclub.sportclub.service.CoachService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Controller
public class ProfilController {
    CoachService coachService;

    public ProfilController(CoachService coachService, CoachCheckInRepo coachCheckInRepo, AdminService adminService) {
        this.coachService = coachService;
        this.coachCheckInRepo = coachCheckInRepo;
        this.adminService = adminService;
    }

    CoachCheckInRepo coachCheckInRepo;
    AdminService adminService;
@GetMapping("/profil")
    public String getProfile(Model model, Authentication authentication){
    String username = authentication.getName();

    UserApp userApp=adminService.loadUserByUsername(username);
    if (userApp.getRoles().getRoleName().equals("COACH")){
        Coach coach = coachService.getCoachByEmail(username);
        List<CheckInCoach> checkInCoaches = coachCheckInRepo.getCheckInByCoach(coach);
        model.addAttribute("userPic",coach.getPic());
        model.addAttribute("userName",coach.getName());
        model.addAttribute("userLname",coach.getLname());
        model.addAttribute("email",coach.getEmail());
        model.addAttribute("id",coach.getId());

        model.addAttribute("coach",coach);
        model.addAttribute("coachCheckIn", checkInCoaches);
    }



    /*String pic=userApp.getPic();
    String name=userApp.getName();
    String lname=userApp.getLname();
    String email=userApp.getEmail();
    int tele= userApp.getTele();
    String cin=userApp.getCin();*/
    model.addAttribute("userPic",userApp.getPic());
    model.addAttribute("userName",userApp.getName());
    model.addAttribute("userLname",userApp.getLname());
    model.addAttribute("email",userApp.getEmail());
    model.addAttribute("user",userApp);
    model.addAttribute("today", LocalDate.now());
    return "profile";
}

    @PostMapping("/editCoachProfile")
    public String editCoachProfil(@Validated Coach c, BindingResult bindingResult){
        if(bindingResult.hasErrors()) return "profile";
        coachService.updateCoach(c);

        return "redirect:/profil";
    }}
