package com.sportclub.sportclub.controller;

import com.sportclub.sportclub.entities.Abonnement;
import com.sportclub.sportclub.entities.Member;
import com.sportclub.sportclub.service.AbonnementService;
import com.sportclub.sportclub.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.beans.PropertyEditorSupport;
import java.time.Period;
import java.util.List;

@Controller
@RequestMapping("/abonnementList")
public class AbonnementController {
    @Autowired
    AbonnementService abonnementService;
    @Autowired
    MemberService memberService;




    @GetMapping("")

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
    /* @RequestMapping(path = {"/abonnementList","/search"})
    public String search( Model model, String keyword) {

        if(keyword!=null) {
            List<Seance> list = service.getSeanceBynName(keyword);
            model.addAttribute("listSeance", list);
        }else {
            List<Seance> list = service.getAllSeance();
            model.addAttribute("listSeance", list);}
        return "seanceList";
    }*/
    @GetMapping("/getMembers")
    public String getMembersByMembership(@RequestParam(name = "id") Long id,Model model){
        List<Member> members=memberService.getMemberByMembership(id);
        model.addAttribute("members",members);
        return "MListByAb";
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
        /*List<Member> members=memberService.getMemberByMembership(id);
        for (Member member:members) {
            member.setAbonnement(null);
        }*/
        abonnementService.deleteAbonnement(id);
        return "redirect:/abonnementList?page="+page+"&keyword="+keyword;
    }
    @GetMapping("/editAbonnement")

    public String editAb(@RequestParam(name = "id") Long id, Model model){
        Abonnement ab=abonnementService.getAboById(id);
        model.addAttribute("abonnement",ab);
        return "EditAbModal";
    }

    @PostMapping("/editAbonnement")
    public String editAb(@Validated Abonnement ab, BindingResult bindingResult){
        if(bindingResult.hasErrors()) return "EditAbModal";
        abonnementService.updateAbonnement(ab);
        return "redirect:/abonnementList";
    }

}
