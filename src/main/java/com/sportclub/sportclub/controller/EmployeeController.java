package com.sportclub.sportclub.controller;

import com.sportclub.sportclub.entities.*;
import com.sportclub.sportclub.repository.CheckInRepo;
import com.sportclub.sportclub.repository.MemberAbonnementRepo;
import com.sportclub.sportclub.repository.MemberRepository;
import com.sportclub.sportclub.repository.PaymentRepo;
import com.sportclub.sportclub.service.*;
import com.sportclub.sportclub.tools.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@PreAuthorize("hasAuthority('EMPLOYEE')")

@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    MemberAbonnementRepo memberAbonnementRepo;
    @Autowired
    CheckInRepo checkInRepo;
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
    PaymentRepo paymentRepo;
    @Autowired
    GymService gymService;
    @Autowired
    CheckInService checkInService;
    @Autowired
    AdminService adminService;
    @Autowired
    FileStorageService storageService;


    @GetMapping("/")
    public String getEmpIndex(Model model) {
        Member member = new Member();
        model.addAttribute("member", member);

        List<Paiement> paiementList = paymentService.getAllPayment();
        List<Paiement> paiements = new ArrayList<>();

        for (Paiement paiement : paiementList) {
            if (paiement.getMember() != null) {
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

    @GetMapping("/members")
    @PreAuthorize("hasAuthority('EMPLOYEE')")
    public String getMembersPage(Model model, @RequestParam(name = "page", defaultValue = "0") int page,
                                 @RequestParam(name = "size", defaultValue = "5") int siz,
                                 @RequestParam(name = "keyword", defaultValue = "") String kw) {
        List<Abonnement> abos = abonnementService.getAllAbos();
        model.addAttribute("abos", abos);
        model.addAttribute("abonnement", new Abonnement());

        Page<Member> pageMember = memberService.findByMemberName(kw, PageRequest.of(page, siz));
        model.addAttribute("listMember", pageMember.getContent());
        model.addAttribute("pages", new int[pageMember.getTotalPages()]);
        model.addAttribute("currentPage", page);
        model.addAttribute("keyword", kw);

        Member member = new Member();

        model.addAttribute("member", member);
        return "employeeInterface/empMembersList";
    }

    @GetMapping("/addMember")
    @PreAuthorize("hasAuthority('EMPLOYEE')")
    public String getAddMemberByEmp(Model model) {
        Member member = new Member();

        model.addAttribute("member", member);
        return "employeeInterface/empMembersList";
    }

    @PostMapping("/addMember")
    @PreAuthorize("hasAuthority('EMPLOYEE')")
    public String addMemberByEmp(@Validated @ModelAttribute("member") Member member, Authentication authentication,
                                 @RequestParam("file") MultipartFile file,
                                 @RequestParam(name = "abonnementId", required = false) Long abonnementId,
                                 RedirectAttributes redirectAttributes) {


        memberService.addMember(member, authentication, file, abonnementId);

        redirectAttributes.addFlashAttribute("successMessage", "Membre ajouté avec succès!");

        return "redirect:/employee/members";
    }

    @GetMapping("/enregistrement")
    @PreAuthorize("hasAuthority('EMPLOYEE')")
    public String getCheckInPage(Model model) {
        List<Seance> seances = seanceService.getAllSeance();
        List<CheckIn> checkIns = checkInService.getCheckInByDate(LocalDate.now());
        LocalTime currentTime = LocalTime.now();
        model.addAttribute("currentTime", currentTime);
        model.addAttribute("checkin", checkIns);
        model.addAttribute("sessions", seances);
        return "employeeInterface/empCheckIns";
    }

    @GetMapping("/search")
    public String search(@RequestParam("searchBy") String searchBy, @RequestParam("keyword") String keyword, Model model) {
        model.addAttribute("member", new Member());
        List<Member> searchResults = new ArrayList<>();

        if ("cin".equals(searchBy)) {
            searchResults = memberService.getMemberByCin(keyword);
        } else if ("tele".equals(searchBy)) {
            searchResults = memberService.getMemberByPhone(keyword);
        } else if (keyword.isEmpty()) {
            return "redirect:/employee/members";
        }

        model.addAttribute("listMember", searchResults);
        model.addAttribute("keyword", keyword);

        return "employeeInterface/empMembersList";
    }

    @GetMapping("/payments")
    public String getPaymentPage(Model model, @RequestParam(name = "page", defaultValue = "0") int page,
                                 @RequestParam(name = "size", defaultValue = "5") int size,
                                 @RequestParam(name = "keyword", defaultValue = "") String kw) {
        Page<Paiement> pageP = paymentService.getPage(kw, PageRequest.of(page, size));
        model.addAttribute("paymentList", pageP.getContent());
        model.addAttribute("pages", new int[pageP.getTotalPages()]);
        model.addAttribute("currentPage", page);
        model.addAttribute("keyword", kw);


        Paiement paiement = new Paiement();
        model.addAttribute("payment", paiement);
        return "employeeInterface/empPayments";
    }

    @RequestMapping(path = {"payments/search"})
    public String search(Model model, String keyword) {

        Paiement paiement = new Paiement();
        model.addAttribute("payment", paiement);
        List<Paiement> list;
        if (keyword != null) {
            list = paymentRepo.findByMemberTeleContains(keyword);
        } else {
            list = paymentService.getAllPayment();
        }
        model.addAttribute("paymentList", list);
        return "employeeInterface/empPayments";
    }

    @PostMapping("payments/pay")
    public String pay(@Validated Paiement paiement, BindingResult bindingResult,Authentication authentication,RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) return "error";

        UserApp user = adminService.loadUserByUsername(authentication.getName());
        String userRole = user.getRoles().getRoleName();

        paiement.setPayedAt(LocalDate.now());
        paiement.setStatus("Payé");
        paiement.setPayedBy("CASH");
        Member member = paiement.getMember();
        member.setStatus("Active");
        memberService.updateMember(member);
        paymentService.updatePayment(paiement);
        Abonnement abonnement = paiement.getAbonnement();

        MemberAbonnement memberMembership = memberAbonnementRepo.findByMemberAndAbonnementAndBookedDate(member, abonnement, paiement.getStart_date());


        if (memberMembership != null) {

            // Update memberMembership status
            memberMembership.setAbStatus("Active");

            memberService.updateMemberAbonnement(memberMembership);

            System.out.println(memberMembership);
            System.out.println("Done!!!!!!!!!");
        }
        redirectAttributes.addFlashAttribute("successMessage", "Paiement effectué avec succès!");


        return "redirect:/employee/payments";

    }
    @GetMapping("/userProfile")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('EMPLOYEE') or hasAuthority('COACH')")

    public String getMemberProfile(@RequestParam(name = "id") Long id, Model model) {
        memberService.updateMemberStatues();
        List<MemberAbonnement> memberAbonnementList = memberAbonnementRepo.findAll();

        for (MemberAbonnement memberAb : memberAbonnementList
        ) {
            if (memberService.isMembershipExpired(memberAb) && !memberAb.getAbStatus().equals("Annulé")) {
                memberAb.setAbStatus("Expiré");
            }
        }
        memberAbonnementRepo.saveAll(memberAbonnementList);

        Member member = memberService.getMemberById(id);
        List<CheckIn> checkIns = checkInRepo.getCheckInByMember(member);
        List<Paiement> paiements = paymentService.getPaymentsByMember(member);
//        Abonnement abonnement = member.getCurrentAbonnement();
        List<MemberAbonnement> memberAbonnements = memberAbonnementRepo.findByMember(member, Sort.by(Sort.Direction.DESC, "id"));

        model.addAttribute("memberships", memberAbonnements);
        model.addAttribute("abos", abonnementService.getAllAbos());
        model.addAttribute("today", LocalDate.now());
        model.addAttribute("checkins", checkIns);
        model.addAttribute("payments", paiements);
        model.addAttribute("user", member);

        return "employeeInterface/empUserProfil";
    }
}
