package com.sportclub.sportclub.controller;

import com.sportclub.sportclub.entities.Abonnement;
import com.sportclub.sportclub.entities.Seance;
import com.sportclub.sportclub.service.SeanceService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.TimeZone;

@Controller
public class SeanceController {

    @Autowired
    SeanceService service;



    @GetMapping("/seanceList")
    public String getSeances(Model model , @RequestParam(name = "page",defaultValue = "0") int page,
                         @RequestParam(name = "size",defaultValue = "5") int size,
                         @RequestParam(name = "keyword",defaultValue = "") String kw
    ) {

        Page<Seance> pageS = service.findBySeanceName(kw, PageRequest.of(page,size));
        model.addAttribute("listSeance",pageS.getContent());
        model.addAttribute("pages",new int[pageS.getTotalPages()]);
        model.addAttribute("currentPage",page);
        model.addAttribute("keyword",kw);
        Seance seance = new Seance();
        model.addAttribute("seance", seance);
        return "seanceList";

    }
    @GetMapping("/addSeance")
    public String getAddSeance(Model model) {
        Seance seance = new Seance();
        model.addAttribute("seance", seance);
        return "seanceList";
    }
    @PostMapping("/addSeance")
    public String addSeance(@Validated Seance seance, BindingResult bindingResult){
        if(bindingResult.hasErrors()) return "seanceList";
        service.addSeance(seance);
        return "redirect:/seanceList";
    }



    @GetMapping("/deleteSeance")
    public String deleteSeance(@RequestParam(name = "id") Long id,String keyword, int page){
        service.deletSeance(id);
        return "redirect:/seanceList?page="+page+"&keyword="+keyword;
    }
    @GetMapping("/editSeance")

    public String editSeance(@RequestParam(name = "id") Long id, Model model){
        Seance seance=service.getSeanceById(id);
        model.addAttribute("seance",seance);
        return "updateSeanceForm";
    }

    @PostMapping("/editSeance")
    public String editseance(@Validated Seance seance, BindingResult bindingResult){
        if(bindingResult.hasErrors()) return "updateSeanceForm";
        service.updateSeance(seance);
        return "redirect:/seanceList";
    }
}
