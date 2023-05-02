package com.sportclub.sportclub.controller;

import com.sportclub.sportclub.entities.Abonnement;
import com.sportclub.sportclub.entities.Coach;
import com.sportclub.sportclub.entities.Member;
import com.sportclub.sportclub.entities.Role;
import com.sportclub.sportclub.service.CoachService;
import jdk.jfr.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
public class CoachController {
    @Autowired
    CoachService coachService;

    @GetMapping("/coachList")
    public String getCoachs(Model model , @RequestParam(name = "page",defaultValue = "0") int page,
                             @RequestParam(name = "size",defaultValue = "5") int size,
                             @RequestParam(name = "keyword",defaultValue = "") String kw
    ) {
        Page<Coach> pageCoach = coachService.findByCoachName(kw, PageRequest.of(page,size));
        model.addAttribute("listCoach",pageCoach.getContent());
        model.addAttribute("pages",new int[pageCoach.getTotalPages()]);
        model.addAttribute("currentPage",page);
        model.addAttribute("keyword",kw);
        Coach CoachForm = new Coach();
        model.addAttribute("CoachForm", CoachForm);
        return "coachList";

    }
    @GetMapping("/addCoach")
    public String getAddCoachPage(Model model) {
        Coach coachForm = new Coach();
        model.addAttribute("CoachForm", coachForm);
        return "coachList";
    }

    @PostMapping("/addCoach")
    public String addCoach(@Validated Coach c, BindingResult bindingResult){
        if(bindingResult.hasErrors()) return "coachList";
        coachService.updateCoach(c);
        return "redirect:/coachList";
    }
    /*@RequestMapping(value = {"/addCoach"}, method = RequestMethod.POST)
    public String saveCoach(Model model,
                               @ModelAttribute("CoachForm") Coach CoachForm) {
        String name = CoachForm.getName();
        String lname = CoachForm.getLname();
        String email = CoachForm.getEmail();
        String adress = CoachForm.getAdress();
        String cin = CoachForm.getCin();
        LocalDate dob = CoachForm.getDob();
        int tele = CoachForm.getTele();
        String password = CoachForm.getPassword();
        String sport_Type = CoachForm.getSport_type();
        List<Role> roles=CoachForm.getRoles();

        if (name != null && name.length() > 0 && lname != null && lname.length() > 0) {
            Coach newCoach = new Coach(name, lname,adress,cin,   dob,  tele,roles,email, password, sport_Type);
            coachService.addCoach(newCoach);
            return "redirect:/coachList";
        }
        model.addAttribute("errorMessage", "Il faut saisir toutes les information");
        return "coachList";
    }*/
    @GetMapping("/deleteCoach")
    public String deleteCoach(@RequestParam(name = "id") Long id,String keyword, int page){
        coachService.deleteCoach(id);
        return "redirect:/coachList?page="+page+"&keyword="+keyword;
    }
    @GetMapping("/editCoach")

    public String editCoach(@RequestParam(name = "id") Long id, Model model){
        Coach coach=coachService.getCoachById(id);
        model.addAttribute("coach",coach);
        return "updateCoachForm";
    }

    @PostMapping("/editCoach")
    public String editCoach(@Validated Coach c, BindingResult bindingResult){
        if(bindingResult.hasErrors()) return "updateCoachForm";
        coachService.updateCoach(c);
        return "redirect:/coachList";
    }


}
