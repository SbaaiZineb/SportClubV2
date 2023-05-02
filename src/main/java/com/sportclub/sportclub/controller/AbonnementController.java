package com.sportclub.sportclub.controller;

import com.sportclub.sportclub.entities.Abonnement;
import com.sportclub.sportclub.entities.Member;
import com.sportclub.sportclub.service.AbonnementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Controller
public class AbonnementController {
    @Autowired
    AbonnementService abonnementService;
    @GetMapping("/abonnementList")
    public String getAbs(Model model ,@RequestParam(name = "page",defaultValue = "0") int page,
                             @RequestParam(name = "size",defaultValue = "5") int size,
                             @RequestParam(name = "keyword",defaultValue = "") String kw
    ) {

        Page<Abonnement> pageAb = abonnementService.findByAboName(kw, PageRequest.of(page,size));
        model.addAttribute("listAb",pageAb.getContent());
        model.addAttribute("pages",new int[pageAb.getTotalPages()]);
        model.addAttribute("currentPage",page);
        model.addAttribute("keyword",kw);
        Abonnement abonnement = new Abonnement();
        model.addAttribute("abonnement", abonnement);
        return "abList";

    }
    @GetMapping("/addAbonnement")
    public String getAddAbonnement(Model model) {
        Abonnement abonnement = new Abonnement();
        model.addAttribute("abonnement", abonnement);
        return "abList";
    }
    @PostMapping("/addAbonnement")
    public String addAb(@Validated Abonnement ab, BindingResult bindingResult){
        if(bindingResult.hasErrors()) return "abList";
        abonnementService.addAb(ab);
        return "redirect:/abonnementList";
    }



    @GetMapping("/deleteAbonnement")
    public String deleteAB(@RequestParam(name = "id") Long id,String keyword, int page){
        abonnementService.deleteAbonnement(id);
        return "redirect:/abonnementList?page="+page+"&keyword="+keyword;
    }
    @GetMapping("/editAbonnement")

    public String editAb(@RequestParam(name = "id") Long id, Model model){
        Abonnement ab=abonnementService.getAboById(id);
        model.addAttribute("abonnement",ab);
        return "updateABForm";
    }

    @PostMapping("/editAbonnement")
    public String editAb(@Validated Abonnement ab, BindingResult bindingResult){
        if(bindingResult.hasErrors()) return "updateABForm";
        abonnementService.updateAbonnement(ab);
        return "redirect:/abonnementList";
    }
 /*@RequestMapping(value = {"/addAbonnement"}, method = RequestMethod.POST)
    public String saveAbonnement(Model model,
                                 @ModelAttribute("abonnement") Abonnement abonnement) {
        String type_ab = abonnement.getNameAb();
        double price = abonnement.getPrice();
        String period = abonnement.getPeriod();

        Abonnement newAb = new Abonnement(type_ab, price, period);
        abonnementService.addAb(newAb);
        return "redirect:/addAbonnement";

    }*/
}
