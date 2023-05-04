package com.sportclub.sportclub.controller;

import com.sportclub.sportclub.entities.Abonnement;
import com.sportclub.sportclub.entities.Coach;
import com.sportclub.sportclub.entities.Member;
import com.sportclub.sportclub.entities.Role;
import com.sportclub.sportclub.service.AbonnementService;
import com.sportclub.sportclub.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

@Controller
public class MemberController {

    @Autowired
    MemberService memberService;
    @Autowired
    AbonnementService abonnementService;

    @GetMapping("/membersList")
    public String getMembers(Model model, @RequestParam(name = "page", defaultValue = "0") int page,
                             @RequestParam(name = "size", defaultValue = "5") int size,
                             @RequestParam(name = "keyword", defaultValue = "") String kw
    ) {

        List<Abonnement> abos = abonnementService.getAllAbos();
        model.addAttribute("abos", abos);
        model.addAttribute("abonnement", new Abonnement());

        Page<Member> pageMember = memberService.findByMemberName(kw, PageRequest.of(page, size));
        model.addAttribute("listMember", pageMember.getContent());
        model.addAttribute("pages", new int[pageMember.getTotalPages()]);
        model.addAttribute("currentPage", page);
        model.addAttribute("keyword", kw);
        Member MemberForm = new Member();
        model.addAttribute("MemberForm", MemberForm);
        return "membersList";

    }

    @GetMapping("/addMember")
    public String getAddPage(Model model) {
        Member MemberForm = new Member();

        model.addAttribute("MemberForm", MemberForm);
        return "membersList";
    }

    /* @RequestMapping(value = {"/addMember"}, method = RequestMethod.POST)
     public String savePersonne(Model model,
                                @ModelAttribute("MemberForm") Member MemberForm ,@RequestParam("pic") MultipartFile file
                                )throws IOException {
             String pic = new String(file.getBytes(), StandardCharsets.UTF_8);
         String name = MemberForm.getName();
         String lname = MemberForm.getLname();
         String email = MemberForm.getEmail();
         String adress = MemberForm.getAdress();
         String cin = MemberForm.getCin();
         LocalDate dob = MemberForm.getDob();
         int tele = MemberForm.getTele();
         String password = MemberForm.getPassword();
         String gender = MemberForm.getGender();
         List<Role> role=MemberForm.getRoles();
         Abonnement abonnement = MemberForm.getAbonnement();
         if (name != null && name.length() > 0 && lname != null && lname.length() > 0) {
             Member newMember = new Member(pic ,name, lname,adress,cin,   dob,  tele,email, password, gender);
             memberService.addMember(newMember);
             return "redirect:/membersList";
         }
         model.addAttribute("errorMessage", "Il faut saisir toutes les information");
         return "membersList";
     }
     @Configuration
     public class AppConfig {
         @Bean
         public MultipartResolver multipartResolver() {
             return new StandardServletMultipartResolver();
         }
     }*/
    @PostMapping("/addMember")
    public String addMember(@Validated Member member, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) return "membersList";
        memberService.updateMember(member);
        return "redirect:/membersList";
    }

    @GetMapping("/deleteMember")
    public String deleteMember(@RequestParam(name = "id") Long id, String keyword, int page) {
        memberService.deletMember(id);
        return "redirect:/membersList?page=" + page + "&keyword=" + keyword;
    }

    @GetMapping("/editMember")

    public String editMember(@RequestParam(name = "id") Long id, Model model) {
        Member member = memberService.getMemberById(id);
        model.addAttribute("member", member);
        return "updateMemberForm";
    }

    @PostMapping("/editMember")
    public String editMember(@Validated Member member, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) return "updateMemberForm";
        memberService.updateMember(member);
        return "redirect:/membersList";
    }
}
