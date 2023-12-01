package com.sportclub.sportclub.controller;

import com.sportclub.sportclub.entities.CheckIn;
import com.sportclub.sportclub.entities.Member;
import com.sportclub.sportclub.entities.Paiement;
import com.sportclub.sportclub.entities.Seance;
import com.sportclub.sportclub.repository.MemberRepository;
import com.sportclub.sportclub.service.*;
import com.sportclub.sportclub.tools.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/employee")
public class EmployeeController {
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
    @Autowired
    FileStorageService storageService;

    @GetMapping("/employeeBar")
    public String getEmployeeInterface(){
        return "partials/employeeBar";
    }

    @GetMapping("/")
    public String getEmpIndex(Model model){
        List<Paiement> paiementList = paymentService.getAllPayment();
        List<Paiement> paiements=new ArrayList<>();

        for(Paiement paiement:paiementList){
            if (paiement.getMember()!=null){
                paiements.add(paiement);
            }
        }
        model.addAttribute("payments", paiements);
        LocalTime currentTime = LocalTime.now();
        model.addAttribute("currentTime", currentTime);

        model.addAttribute("today", LocalDate.now());
        List<Member> members = memberService.getAllMembers();
        model.addAttribute("members", members);
        List<CheckIn> checkIns = checkInService.getAllCheckIns();
        model.addAttribute("checkin", checkIns);
        return "employeeInterface/empIndex";
    }


  /*  @GetMapping("/images/{filename:.+}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) {
        Resource file;

        // Check if the filename corresponds to the image in the "img" directory
        if ("default-user.png".equals(filename)) {
            file = new ClassPathResource("static/img/" + filename);
        } else {
            file = storageService.load(filename);
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }*/
}
