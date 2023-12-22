package com.sportclub.sportclub.controller;

import com.lowagie.text.DocumentException;
import com.sportclub.sportclub.entities.Gym;
import com.sportclub.sportclub.entities.Member;
import com.sportclub.sportclub.entities.UserApp;
import com.sportclub.sportclub.repository.GymRepo;
import com.sportclub.sportclub.repository.PaymentRepo;
import com.sportclub.sportclub.service.*;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.TemplateEngine;
import com.sportclub.sportclub.entities.Paiement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import javax.swing.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.List;

@Controller
@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('EMPLOYEE')")
public class PaymentController {
    @Autowired
    PaymentService paymentService;
    @Autowired
    AdminService adminService;
    @Autowired
    PaymentRepo paymentRepo;
    @Autowired
    MemberService memberService;
    @Autowired
    GymService gymService;

    @GetMapping("/payments")
    public String getPayment(Model model, @RequestParam(name = "page", defaultValue = "0") int page,
                             @RequestParam(name = "size", defaultValue = "5") int size,
                             @RequestParam(name = "keyword", defaultValue = "") String kw
    ) {

        Page<Paiement> pageP = paymentService.getPage(kw, PageRequest.of(page, size));
        model.addAttribute("paymentList", pageP.getContent());
        model.addAttribute("pages", new int[pageP.getTotalPages()]);
        model.addAttribute("currentPage", page);
        model.addAttribute("keyword", kw);


        Paiement paiement = new Paiement();
        model.addAttribute("payment", paiement);
        return "paymentList";

    }


    @GetMapping("payments/pay")
    public String getPay(@RequestParam(name = "id") Long id, Model model) {
        LocalDate localDate = LocalDate.now();
        model.addAttribute("date", localDate);
        Paiement paiement = paymentService.getPaymentById(id);
        model.addAttribute("payment", paiement);
        return "paymentModal";
    }

    @RequestMapping(path = {"/payments/search"})
    public String search(Model model, String keyword) {

        Paiement paiement = new Paiement();
        model.addAttribute("payment", paiement);
        List<Paiement> list;
        if (keyword != null) {
            list = paymentRepo.findByMemberNameContains(keyword);
        } else {
            list = paymentService.getAllPayment();
        }
        model.addAttribute("paymentList", list);
        return "paymentList";
    }

    @PostMapping("payments/pay")
    public String pay(@Validated Paiement paiement, BindingResult bindingResult, Authentication authentication) {
        if (bindingResult.hasErrors()) return "error";


        UserApp user = adminService.loadUserByUsername(authentication.getName());
        String userRole = user.getRoles().getRoleName();


        paiement.setPayedAt(LocalDate.now());
        paiement.setStatue("Payé");
        paiement.setPayedBy("CASH");
        Member member = paiement.getMember();
        member.setStatue("Active");
        memberService.updateMember(member);
        paymentService.updatePayment(paiement);
        if (userRole.equals("EMPLOYEE")) {
            return "redirect:/employee/payments";
        }
        return "redirect:/payments";
    }

    @Autowired
    TemplateEngine templateEngine;

    @GetMapping("/invoice")
    public void invoice(HttpServletResponse response, @RequestParam(name = "id") Long id) throws DocumentException, IOException {
        Paiement paiement = paymentService.getPaymentById(id);
        Context context = new Context();

        Gym gym = gymService.getById(1L);

        context.setVariable("gym", gym);
        String imageFilename = gym.getLogo();
        if (!imageFilename.isEmpty()) {

            imageFilename = imageFilename.trim();
            // Convert the image to base64 and add it to the context

            Path imagePath = Paths.get("uploads", imageFilename);
            String base64Image = convertToBase64(imagePath);

            context.setVariable("base64Image", base64Image);
        }
        context.setVariable("payment", paiement);

        String htmlContent = templateEngine.process("invoice", context);

        // Create a ByteArrayOutputStream to hold the PDF content
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // Use Flying Saucer to convert HTML to PDF
        ITextRenderer renderer = new ITextRenderer();

        renderer.setDocumentFromString(htmlContent);
        renderer.layout();
        renderer.createPDF(outputStream);

        // Set the response headers for PDF download
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=recu.pdf");

        // Write the PDF content to the response output stream
        outputStream.writeTo(response.getOutputStream());
        outputStream.flush();
        outputStream.close();

    }

    public String convertToBase64(Path imagePath) throws IOException {
        try {
            // Check if the filename is not empty
            if (!imagePath.getFileName().toString().isEmpty()) {
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

    @Autowired
    InvoiceService invoiceService;
  /*  @PostMapping("/invoice")
    public void generateInvoicePdf(HttpServletResponse response, File contentToGenerate) throws IOException, DocumentException {
        contentToGenerate = new File("src/main/resources/templates/invoice.html");
        ByteArrayInputStream byteArrayInputStream = invoiceService.convertHtmlToPdf(contentToGenerate);
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=file.pdf");
        IOUtils.copy(byteArrayInputStream, response.getOutputStream());

    }*/


    @RequestMapping(value = "/payments/cancel", method = {RequestMethod.GET, RequestMethod.POST})

    public String cancelPayment(@RequestParam(name = "payId") Long id) {

        try {

            Paiement paiement = paymentService.getPaymentById(id);
            paiement.setStatue("Annulée");
            paymentService.updatePayment(paiement);

        } catch (Exception e) {
            System.out.println("Something is wrong !! " + e);
        }
        return "redirect:/payments";
    }
}
