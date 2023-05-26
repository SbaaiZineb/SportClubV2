package com.sportclub.sportclub.controller;

import com.lowagie.text.DocumentException;
import com.sportclub.sportclub.entities.Abonnement;
import com.sportclub.sportclub.entities.Member;
import com.sportclub.sportclub.entities.Paiement;
import com.sportclub.sportclub.entities.Role;
import com.sportclub.sportclub.service.AbonnementService;
import com.sportclub.sportclub.service.MemberService;
import com.sportclub.sportclub.service.PaymentService;
import com.sportclub.sportclub.service.RoleService;
import com.sportclub.sportclub.tools.FileStorageService;
import com.sportclub.sportclub.tools.MemberPdf;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Controller
public class MemberController {
@Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    MemberService memberService;
    @Autowired
    AbonnementService abonnementService;
    @Autowired
    FileStorageService storageService;

    @Autowired
    PaymentService paymentService;
    @Autowired
    RoleService roleService;

    @RequestMapping(value = "/membersList",method = RequestMethod.GET)
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
        Member memberForm = new Member();
        model.addAttribute("memberForm", memberForm);
        return "membersList";

    }

    @GetMapping("/log")
    public String getSide() {
        return "login";
    }

    @RequestMapping(path = { "/membersList","/search"})
    public String search(Model model, String keyword) {

        Member MemberForm = new Member();
        model.addAttribute("MemberForm", MemberForm);
        List<Member> list;
        if (keyword != null) {
            list = memberService.getMemberBynName(keyword);
        } else {
            list = memberService.getAllMembers();
        }
        model.addAttribute("listMember", list);
        return "membersList";
    }

    @RequestMapping(value = "/addMember",method = RequestMethod.GET)
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SUBADMIN')")
    public String getAddPage(Model model) {
        Member memberForm = new Member();
        model.addAttribute("memberForm", memberForm);
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
    @PreAuthorize("hasAuthority('ADMIN')or hasAuthority('SUBADMIN')")
    public String addMember(@Validated @ModelAttribute("memberForm") Member memberForm, BindingResult bindingResult, @RequestParam("file") MultipartFile file,Model model) {
        if (bindingResult.hasErrors()) return "membersList";
        if (memberService.getByEmail(memberForm.getEmail())){
            System.out.println("Username already exist");
            bindingResult.rejectValue("email", "error.memberForm", "Username already exists");
            model.addAttribute("usernameExistsError", "Username already exists");
            return "redirect:/addMember";
        }
        LocalDate localDate = LocalDate.now();
        memberForm.setCreatedAt(localDate);
        memberForm.setPic(file.getOriginalFilename());
        String password= memberForm.getPassword();
        memberForm.setPassword(passwordEncoder.encode(password));
        List<Role> roles=roleService.findAllRoles();

        for (Role role:roles
             ) {
            if (role.getRoleName().equals("ADHERENT")){

                memberForm.setRoles(role);
            }
        }
        memberService.addMember(memberForm);
        Paiement paiement = new Paiement();
        paiement.setStart_date(localDate);
        paiement.setStatue("Impayé");
        paiement.setAbonnement(memberForm.getAbonnement());
        String per = paiement.getAbonnement().getPeriod();
//       int period=Integer.parseInt(per);
//        LocalDate end=paiement.getStart_date().plusMonths(period);
//        paiement.setEnd_date(end);
        switch (per) {
            case "12" -> {
                LocalDate end = paiement.getStart_date().plusYears(1);
                paiement.setEnd_date(end);
            }
            case "3" -> {
                LocalDate end = paiement.getStart_date().plusMonths(3);
                paiement.setEnd_date(end);
            }
            case "1" -> {
                LocalDate end = paiement.getStart_date().plusMonths(1);
                paiement.setEnd_date(end);
            }
            case "6" -> {
                LocalDate end = paiement.getStart_date().plusMonths(6);
                paiement.setEnd_date(end);
            }
            case "2" -> {
                LocalDate end = paiement.getStart_date().plusMonths(2);
                paiement.setEnd_date(end);
            }
        }
        paiement.setMember(memberForm);
        storageService.save(file);
        paymentService.addPayement(paiement);

        return "redirect:/membersList";
    }

    @GetMapping("/images/{filename:.+}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) {
        Resource file = storageService.load(filename);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @GetMapping("/deleteMember")
    @PreAuthorize("hasAuthority('ADMIN')or hasAuthority('SUBADMIN')")
    public String deleteMember(@RequestParam(name = "id") Long id, String keyword, int page) {
        memberService.deletMember(id);
        return "redirect:/membersList?page=" + page + "&keyword=" + keyword;
    }

    @GetMapping("/editMember")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SUBADMIN')")
    public String editMember(@RequestParam(name = "id") Long id, Model model) {
        List<Abonnement> abos = abonnementService.getAllAbos();
        model.addAttribute("abos", abos);
        model.addAttribute("abonnement", new Abonnement());
        Member member = memberService.getMemberById(id);
        model.addAttribute("member", member);
        return "updateMemberModal";
    }

    @PostMapping("/editMember")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SUBADMIN')")
    public String editMember(@Validated Member member, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) return "updateMemberModal";
        memberService.updateMember(member);
        return "redirect:/membersList";
    }

    @GetMapping("membersList/export/pdf")
    public void exportToPDF(HttpServletResponse response) throws DocumentException, IOException {
        response.setContentType("application/pdf");


        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=users_" + currentDateTime + ".pdf";
        response.setHeader(headerKey, headerValue);

        List<Member> listUsers = memberService.getAllMembers();
        MemberPdf exporter = new MemberPdf(listUsers);
        exporter.export(response);
    }
    @PostMapping("/deleteCells")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SUBADMIN')")
    public String deleteCells(@RequestParam("selectedCells") Long[] selectedCells) {
        // Perform the delete operation using the selected cell IDs

        for (Long cellId : selectedCells) {

            memberService.deletMember(cellId);
        }

        // Redirect to a success page or return a response as needed
        return "redirect:/membersList";
    }


}
