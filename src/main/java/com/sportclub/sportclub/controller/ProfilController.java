package com.sportclub.sportclub.controller;

import com.sportclub.sportclub.entities.CheckInCoach;
import com.sportclub.sportclub.entities.Coach;
import com.sportclub.sportclub.entities.UserApp;
import com.sportclub.sportclub.repository.CoachCheckInRepo;
import com.sportclub.sportclub.service.AdminService;
import com.sportclub.sportclub.service.CoachCheckInService;
import com.sportclub.sportclub.service.CoachService;
import com.sportclub.sportclub.tools.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
public class ProfilController {
    @Autowired
    PasswordEncoder passwordEncoder;
    CoachService coachService;
@Autowired
    FileStorageService fileStorageService;
    public ProfilController(CoachService coachService, CoachCheckInRepo coachCheckInRepo, AdminService adminService) {
        this.coachService = coachService;
        this.coachCheckInRepo = coachCheckInRepo;
        this.adminService = adminService;
    }

    CoachCheckInRepo coachCheckInRepo;
    AdminService adminService;
@GetMapping("/profil")
@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('EMPLOYEE') or hasAuthority('COACH')")

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
    public String editCoachProfil(@Validated Coach c, BindingResult bindingResult, @RequestParam(name = "file")MultipartFile file){
        if(bindingResult.hasErrors()) return "profile";
        Coach existingCoach = coachService.getCoachById(c.getId());
        if (file != null && !file.isEmpty()) {

            c.setPic(file.getOriginalFilename());
            fileStorageService.save(file);
        } else {

            if (existingCoach != null) {
                c.setPic(existingCoach.getPic());
            }
        }
        coachService.updateCoach(c);

        return "redirect:/profil";
    }

    @GetMapping("/passwordUpdate")
    public String getUpdatePass(Authentication authentication,Model model){
        String username = authentication.getName();

        UserApp userApp=adminService.loadUserByUsername(username);
        model.addAttribute("user",userApp);

    return "profile";
    }
    @PostMapping("/passwordUpdate")
    public String updatePassword(@RequestParam(name = "id") Long id, @RequestParam(name = "password") String password, RedirectAttributes redirectAttributes){
    UserApp user=adminService.getAdminById(id);
    try {
        user.setPassword(passwordEncoder.encode(password));
        adminService.updateAdmin(user);
        System.out.println("OK!!!!!!!!!!!!!!");
        redirectAttributes.addFlashAttribute("successMessage", "Mot de passe actualisé avec succès!");

    }catch (Exception e){
        System.out.println("something wrong !!!!!!!!!!!");
    }
    return "redirect:/profil";
    }

}
