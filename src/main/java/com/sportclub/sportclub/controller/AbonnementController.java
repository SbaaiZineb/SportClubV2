package com.sportclub.sportclub.controller;

import com.sportclub.sportclub.entities.Abonnement;
import com.sportclub.sportclub.entities.Member;
import com.sportclub.sportclub.entities.Paiement;
import com.sportclub.sportclub.repository.AbonnementRepo;
import com.sportclub.sportclub.service.AbonnementService;
import com.sportclub.sportclub.service.MemberService;
import com.sportclub.sportclub.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('EMPLOYEE')")
public class AbonnementController {
    @Autowired
    AbonnementService abonnementService;
    @Autowired
    PaymentService paymentService;
    @Autowired
    MemberService memberService;
@Autowired
    AbonnementRepo abonnementRepo;



    @GetMapping("/abonnementList")

    public String getAbs(Model model ,@RequestParam(name = "page",defaultValue = "0") int page,
                             @RequestParam(name = "size",defaultValue = "7") int size,
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
    @PostMapping("/deleteAbs")
    public String deleteCells(@RequestParam("selectedab") Long[] selectedab) {
        // Perform the delete operation using the selected cell IDs

        for (Long cellId : selectedab) {

            abonnementService.deleteAbonnement(cellId);

        }

        // Redirect to a success page or return a response as needed
        return "redirect:/abonnementList";
    }
     @RequestMapping(path = {"/abonnementList/search"})
    public String search( Model model, String ab) {

         Abonnement abonnement = new Abonnement();
         model.addAttribute("abonnement", abonnement);

        if(ab!=null) {
            List<Abonnement> list = abonnementRepo.findByNameAbContains(ab);
            model.addAttribute("listAb", list);
        }else {
            List<Abonnement> list = abonnementService.getAllAbos();
            model.addAttribute("listAb", list);}
        return "abList";
    }
    @GetMapping("/abonnementList/getMembers")
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
    public String addAb(@Validated Abonnement ab, BindingResult bindingResult, RedirectAttributes redirectAttributes){
        if(bindingResult.hasErrors()) return "abList";
        abonnementService.addAb(ab);
        redirectAttributes.addFlashAttribute("successMessage", "Abonnement ajouté avec succès!");

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
        return "EditAbModal";
    }

    @PostMapping("/editAbonnement")
    public String editAb(@Validated Abonnement ab, BindingResult bindingResult,RedirectAttributes redirectAttributes){
        if(bindingResult.hasErrors()) return "error";
        abonnementService.updateAbonnement(ab);
        redirectAttributes.addFlashAttribute("successMessage", "Abonnement actualisé avec succès!");

        return "redirect:/abonnementList";
    }

}
