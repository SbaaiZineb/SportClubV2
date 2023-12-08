package com.sportclub.sportclub.controller;

import com.lowagie.text.DocumentException;
import com.sportclub.sportclub.entities.Abonnement;
import com.sportclub.sportclub.entities.Gym;
import com.sportclub.sportclub.entities.Member;
import com.sportclub.sportclub.repository.AdminRepo;
import com.sportclub.sportclub.repository.CheckInRepo;
import com.sportclub.sportclub.service.*;
import com.sportclub.sportclub.tools.FileStorageService;
import com.sportclub.sportclub.tools.MemberPdf;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class MemberController {

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

    GymService gymService;
    public MemberController( MemberService memberService, AbonnementService abonnementService, FileStorageService storageService, PaymentService paymentService, RoleService roleService, AdminRepo adminRepo, NotificationService notificationService, FileStorageService fileService,GymService gymService) {

        this.memberService = memberService;
        this.abonnementService = abonnementService;
        this.storageService = storageService;
        this.paymentService = paymentService;
        this.roleService = roleService;
        this.adminRepo = adminRepo;
        this.notificationService = notificationService;
        this.fileService = fileService;
        this.gymService = gymService;
    }

    AdminRepo adminRepo;
    NotificationService notificationService;

    @RequestMapping(value = "/membersList", method = RequestMethod.GET)
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('EMPLOYEE') or hasAuthority('COACH')")
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



   /* @GetMapping("/membersList/search")
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
    }*/

    @GetMapping("/membersList/search")
    public String search(@RequestParam("keyword") String keyword, Model model) {
        model.addAttribute("memberForm", new Member());
        List<Member> searchResults = memberService.getMemberBynName(keyword);
        model.addAttribute("listMember", searchResults);
        return "membersList";
    }

    @RequestMapping(value = "/addMember", method = RequestMethod.GET)
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('EMPLOYEE')")
    public String getAddPage(Model model) {
        Member memberForm = new Member();
        model.addAttribute("memberForm", memberForm);
        return "membersList";
    }

    @PostMapping("/addMember")
    @PreAuthorize("hasAuthority('ADMIN')or hasAuthority('EMPLOYEE')")
    public String addMember(@Validated @ModelAttribute("memberForm") Member memberForm, Authentication authentication, BindingResult bindingResult, @RequestParam("file") MultipartFile file, Model model) {
        if (bindingResult.hasErrors()) return "membersList";

        memberService.addMember(memberForm,authentication,file);


        return "redirect:/membersList";
    }

    @GetMapping("/images/{filename:.+}")
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
    }

    @GetMapping("/deleteMember")
    @PreAuthorize("hasAuthority('ADMIN')or hasAuthority('EMPLOYEE')")
    public String deleteMember(@RequestParam(name = "id") Long id, String keyword, int page, Model model) {
       Member member=memberService.getMemberById(id);

                memberService.deletMember(id);
                if (member.getPic()!=null){
                    fileStorageService.deleteFile(member.getPic());

                }


        return "redirect:/membersList?page=" + page + "&keyword=" + keyword;
    }

    @GetMapping("/editMember")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('EMPLOYEE')")
    public String editMember(@RequestParam(name = "id") Long id, Model model) {
        List<Abonnement> abos = abonnementService.getAllAbos();
        model.addAttribute("abos", abos);
        model.addAttribute("abonnement", new Abonnement());
        Member member = memberService.getMemberById(id);
        model.addAttribute("member", member);
        return "updateMemberModal";
    }

    @PostMapping("/editMember")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('EMPLOYEE')")
    public String editMember(@Validated Member member, BindingResult bindingResult, @RequestParam("file") MultipartFile file) {
        if (bindingResult.hasErrors()) return "error";
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

        Gym gym = gymService.getById(1L);

        List<Member> listUsers = memberService.getAllMembers();
        MemberPdf exporter = new MemberPdf(listUsers);
        exporter.export(response);
    }

    @PostMapping("/deleteCells")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('EMPLOYEE')")
    public String deleteCells(@RequestParam("selectedCells") Long[] selectedCells) {
        // Perform the delete operation using the selected cell IDs

        for (Long cellId : selectedCells) {

            Member memberById =memberService.getMemberById(cellId);
            try {
                memberService.deletMember(cellId);
                if (memberById.getPic()!=null){
                    fileStorageService.deleteFile(memberById.getPic());

                }

            } catch (Exception e) {
                System.out.println("Something Wrong!!!!! "+e);
                return "error";
            }
        }

        // Redirect to a success page or return a response as needed
        return "redirect:/membersList";
    }

    //Check Email Existence
    @GetMapping("/checkEmailExists")
    public ResponseEntity<Map<String, Boolean>> checkEmailExists(@RequestParam String email) {
        boolean emailExists = memberService.checkEmail(email);

        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", emailExists);
        System.out.println(memberService.checkEmail("ahmad@ahmed.com"));
        System.out.println(memberService.checkEmail("karim"));
        return ResponseEntity.ok(response);
    }

    //Check Cin existence
    @GetMapping("/checkCinExists")
    public ResponseEntity<Map<String, Boolean>> checkCinExists(@RequestParam String cin) {
        boolean cinExists = memberService.checkCinExist(cin);

        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", cinExists);
        return ResponseEntity.ok(response);
    }


}
