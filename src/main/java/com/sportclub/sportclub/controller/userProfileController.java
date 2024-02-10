package com.sportclub.sportclub.controller;


import com.lowagie.text.DocumentException;
import com.sportclub.sportclub.entities.*;
import com.sportclub.sportclub.repository.CheckInRepo;
import com.sportclub.sportclub.repository.CoachCheckInRepo;
import com.sportclub.sportclub.repository.MemberAbonnementRepo;
import com.sportclub.sportclub.repository.PaymentRepo;
import com.sportclub.sportclub.service.*;
import com.sportclub.sportclub.tools.FileStorageService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Controller

public class userProfileController {
    @Autowired
    TemplateEngine templateEngine;

    @Autowired
    MemberAbonnementRepo memberAbonnementRepo;
    @Autowired
    MemberService memberService;
    @Autowired
    PaymentService paymentService;
    @Autowired
    CheckInRepo checkInRepo;
    @Autowired
    PaymentRepo paymentRepo;
    @Autowired
    AdminService adminService;
    @Autowired
    CoachCheckInRepo coachCheckInRepo;
    @Autowired
    CoachService coachService;
    @Autowired
    AbonnementService abonnementService;
    @Autowired
    FileStorageService fileStorageService;

    @GetMapping("/membersList/userProfile")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('EMPLOYEE') or hasAuthority('COACH')")

    public String getMemberProfile(@RequestParam(name = "id") Long id, Model model) {
        memberService.updateMembershipStatus();
        memberService.updateMemberStatues();


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
        MemberAbonnement membership = new MemberAbonnement();
        model.addAttribute("memberAbonnement", membership);


        return "userProfile";
    }


    @GetMapping("/coachProfile")
    public String getCoachProfile(@RequestParam(name = "id") Long id, Model model) {

        UserApp userApp = adminService.getAdminById(id);


        model.addAttribute("user", userApp);
        Coach coach = coachService.getCoachById(id);
        List<CheckInCoach> checkInCoaches = coachCheckInRepo.getCheckInByCoach(coach);
        model.addAttribute("checkins", checkInCoaches);
        model.addAttribute("today", LocalDate.now());

        return "coachProfile";

    }

    @PostMapping("/membersList/userProfile/addAbo")
    public String updateMembership(@Validated @ModelAttribute("membership") MemberAbonnement memberAbonnement, @RequestParam(name = "userId") Long userId,
                                   @RequestParam(name = "abonnementId", required = false) Long abonnementId, Authentication authentication) {
        UserApp user = adminService.loadUserByUsername(authentication.getName());
        String userRole = user.getRoles().getRoleName();

        try {
            Member member = memberService.getMemberById(userId);


            memberAbonnement.setBookedDate(LocalDate.now());
            if (member.getMemberAbonnements() == null) {
                member.setMemberAbonnements(new ArrayList<>());
            }
            memberAbonnement.setMember(member);
            Abonnement abonnement = abonnementService.getAboById(abonnementId);
            memberAbonnement.setAbonnement(abonnement);
            memberAbonnement.setMontant(abonnement.getPrice());
            memberAbonnement.setAbStatus("En attente");
            memberAbonnement.setNbrSessionCarnet(abonnement.getNbrSeance());

            member.getMemberAbonnements().add(memberAbonnement);


            memberService.updateMember(member);
            //Add new payment
            Paiement paiement = new Paiement();
            paiement.setMember(member);
            paiement.setStart_date(memberAbonnement.getStartDate());
            paiement.setEnd_date(memberAbonnement.getEndDate());
            paiement.setAbonnement(abonnement);
            paiement.setMontant(abonnement.getPrice());
            paiement.setStatus("Impayé");

//            String per = paiement.getAbonnement().getPeriod();
//            SetPayEndDate sPD = new SetPayEndDate();
//            sPD.setPayEndDate(per, paiement);
            paymentService.addPayement(paiement);
            memberAbonnement.setPaiement(paiement);
            memberAbonnementRepo.save(memberAbonnement);


        } catch (Exception e) {
            System.out.println("Something wrong!!! " + e);
        }
        if (userRole.equals("EMPLOYEE")) {
            return "redirect:/employee/userProfile?id=" + userId;
        }

        return "redirect:/membersList/userProfile?id=" + userId;
    }

    @GetMapping("/profilPdf")
    public void profilPdf(HttpServletResponse response, @RequestParam(name = "id") Long id) throws DocumentException, IOException {
        Member member = memberService.getMemberById(id);
        Context context = new Context();


        String imageFilename = member.getPic();
        if (imageFilename != null && !imageFilename.isEmpty()) {

            imageFilename = imageFilename.trim();
            // Convert the image to base64 and add it to the context

            Path imagePath = Paths.get("uploads", imageFilename);
            String base64Image = convertToBase64(imagePath);

            context.setVariable("base64Image", base64Image);
        }
        context.setVariable("user", member);

        String htmlContent = templateEngine.process("ProfilePdf", context);

        // Create a ByteArrayOutputStream to hold the PDF content
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // Use Flying Saucer to convert HTML to PDF
        ITextRenderer renderer = new ITextRenderer();

        renderer.setDocumentFromString(htmlContent);
        renderer.layout();
        renderer.createPDF(outputStream);

        // Set the response headers for PDF download
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=member" + member.getName() + "_" + member.getLname() + ".pdf");

        // Write the PDF content to the response output stream
        outputStream.writeTo(response.getOutputStream());
        outputStream.flush();
        outputStream.close();


    }

    public String convertToBase64(Path imagePath) throws IOException {
        try {
            // Check if the filename is not empty
            if (imagePath.getFileName() != null && !imagePath.getFileName().toString().isEmpty()) {
                // Check if the file exists
                if (Files.exists(imagePath)) {
                    byte[] imageBytes = Files.readAllBytes(imagePath);
                    return Base64.getEncoder().encodeToString(imageBytes);
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @PostMapping("/editNotes")
    public String editNotes(@ModelAttribute("user") Member user) {

        Member existingUser = memberService.getMemberById(user.getId());

        if (existingUser != null) {
            existingUser.setNotes(user.getNotes());

            memberService.updateMember(existingUser);
        }

        return "redirect:/membersList/userProfile?id=" + user.getId();
    }

    @GetMapping("/membersList/userProfile/editDate")
    public String getEditDate(@RequestParam Long id, Model model) {
        MemberAbonnement memberAbonnement = memberAbonnementRepo.findById(id).orElse(null);
        model.addAttribute("membership", memberAbonnement);

        return "editDatesModal";
    }

    @PostMapping("/membersList/userProfile/editDate")
    public String editDate(@Validated @ModelAttribute("membership") MemberAbonnement membership) {
        Long userId = membership.getMember().getId();
        LocalDate currentDate = LocalDate.now();
        try {
            LocalDate startDate = membership.getStartDate();
            LocalDate endDate = membership.getEndDate();

            if (endDate != null && currentDate.isAfter(endDate)) {
                membership.setAbStatus("Expiré");

            } else if (startDate.isAfter(currentDate) && (!membership.getAbStatus().equals("En attente"))) {
                membership.setAbStatus("Programmé");

            } else if (!currentDate.isBefore(startDate) && (!membership.getAbStatus().equals("En attente"))) {
                membership.setAbStatus("Active");
            }
            memberAbonnementRepo.save(membership);
            Paiement paiement = membership.getPaiement();
            paiement.setStart_date(membership.getStartDate());
            paiement.setEnd_date(membership.getEndDate());
            paymentService.updatePayment(paiement);

        } catch (Exception e) {
            System.out.println("Exception, Saving the membership's new dates is not done: " + e);
        }

        return "redirect:/membersList/userProfile?id=" + userId;
    }


}
