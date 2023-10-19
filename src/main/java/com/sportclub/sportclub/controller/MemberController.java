package com.sportclub.sportclub.controller;

import com.lowagie.text.DocumentException;
import com.sportclub.sportclub.entities.*;
import com.sportclub.sportclub.repository.AdminRepo;
import com.sportclub.sportclub.repository.CheckInRepo;
import com.sportclub.sportclub.service.*;
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
import org.springframework.security.core.Authentication;
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
import java.time.LocalDateTime;

import java.util.Date;
import java.util.List;

@Controller
public class MemberController {
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    FileStorageService fileStorageService;
    @Autowired
    MemberService memberService;
    AbonnementService abonnementService;
    FileStorageService storageService;
    @Autowired
    CheckInRepo checkInRepo;




    @Autowired
    CheckInService checkInService;
    PaymentService paymentService;
    RoleService roleService;
    FileStorageService fileService;
    public MemberController(PasswordEncoder passwordEncoder, MemberService memberService, AbonnementService abonnementService, FileStorageService storageService, PaymentService paymentService, RoleService roleService, AdminRepo adminRepo, NotificationService notificationService,FileStorageService fileService) {
        this.passwordEncoder = passwordEncoder;
        this.memberService = memberService;
        this.abonnementService = abonnementService;
        this.storageService = storageService;
        this.paymentService = paymentService;
        this.roleService = roleService;
        this.adminRepo = adminRepo;
        this.notificationService = notificationService;
        this.fileService=fileService;
    }

    AdminRepo adminRepo;
    NotificationService notificationService;

    @RequestMapping(value = "/membersList",method = RequestMethod.GET)
    public String getMembers(Model model, @RequestParam(name = "page", defaultValue = "0") int page,
                             @RequestParam(name = "size", defaultValue = "5") int siz,
                             @RequestParam(name = "keyword", defaultValue = "") String kw
    ) {

        List<Abonnement> abos = abonnementService.getAllAbos();
        model.addAttribute("abos", abos);
        model.addAttribute("abonnement", new Abonnement());

        Page<Member> pageMember = memberService.findByMemberName(kw, PageRequest.of(page, siz));
        model.addAttribute("listMember", pageMember.getContent());
        model.addAttribute("pages", new int[pageMember.getTotalPages()]);
        model.addAttribute("currentPage", page);
        model.addAttribute("keyword", kw);
        Member memberForm = new Member();
        model.addAttribute("memberForm", memberForm);
        return "membersList";

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

    @PostMapping("/addMember")
    @PreAuthorize("hasAuthority('ADMIN')or hasAuthority('SUBADMIN')")
    public String addMember(@Validated @ModelAttribute("memberForm") Member memberForm, Authentication authentication, BindingResult bindingResult, @RequestParam("file") MultipartFile file, Model model) {
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
        Notification notification = new Notification();
        String username=authentication.getName();
        notification.setTimestamp(LocalDateTime.now());
        notification.setMessage(username + " a ajouté un nouveau adherent");
        List<UserApp> userApps=adminRepo.findByRolesRoleNameContains("ADMIN");
        notification.setRecipient(userApps);
        notification.setTitle("Nouveau Adherent");
        notificationService.addNotification(notification);
        Paiement paiement = new Paiement();
        paiement.setStart_date(localDate);
        paiement.setStatue("Impayé");
        paiement.setAbonnement(memberForm.getAbonnement());
        String per = paiement.getAbonnement().getPeriod();
//       int period=Integer.parseInt(per);
//        LocalDate end=paiement.getStart_date().plusMonths(period);
//        paiement.setEnd_date(end);
        setPayEndDate(per,paiement);
        paiement.setMember(memberForm);
        storageService.save(file);
        paymentService.addPayement(paiement);

        return "redirect:/membersList";
    }
public void setPayEndDate(String per, Paiement paiement){
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

        Member member = memberService.getMemberById(id);
        List<CheckIn> checks = checkInService.getByMemberCheck(member);
        checkInRepo.deleteAll(checks);
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
    public String editMember(@Validated Member member, BindingResult bindingResult,@RequestParam("file") MultipartFile file) {
        if (bindingResult.hasErrors()) return "updateMemberModal";
        Member existingMember = memberService.getMemberById(member.getId());
        if (file != null && !file.isEmpty()) {

            member.setPic(file.getOriginalFilename());
            fileStorageService.save(file);
        } else {

            if (existingMember != null) {
                member.setPic(existingMember.getPic());
            }
        }
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
