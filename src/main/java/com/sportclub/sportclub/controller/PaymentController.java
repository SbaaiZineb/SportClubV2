package com.sportclub.sportclub.controller;

import com.lowagie.text.DocumentException;
import com.sportclub.sportclub.entities.Gym;
import com.sportclub.sportclub.entities.Member;
import com.sportclub.sportclub.repository.GymRepo;
import com.sportclub.sportclub.repository.PaymentRepo;
import com.sportclub.sportclub.service.GymService;
import com.sportclub.sportclub.service.InvoiceService;
import com.sportclub.sportclub.service.MemberService;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.thymeleaf.TemplateEngine;
import com.sportclub.sportclub.entities.Paiement;
import com.sportclub.sportclub.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SUBADMIN')")
public class PaymentController {
    @Autowired
    PaymentService paymentService;
    @Autowired
    PaymentRepo paymentRepo;
    @Autowired
    MemberService memberService;
    @Autowired
    GymService gymService;

    @GetMapping("/paymentList")
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


    @GetMapping("paymentList/pay")
    public String getPay(@RequestParam(name = "id") Long id, Model model) {
        LocalDate localDate = LocalDate.now();
        model.addAttribute("date", localDate);
        Paiement paiement = paymentService.getPaymentById(id);
        model.addAttribute("payment", paiement);
        return "paymentModal";
    }

    @RequestMapping(path = {"/paymentList/search"})
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

    @PostMapping("paymentList/pay")
    public String addAb(@Validated Paiement paiement, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) return "paymentModal";
        paiement.setPayedAt(LocalDate.now());
        paiement.setStatue("Payé");
        Member member= paiement.getMember();
        member.setStatue("Active");
        memberService.updateMember(member);
        paymentService.updatePayment(paiement);
        return "redirect:/paymentList";
    }

    @Autowired
    TemplateEngine templateEngine;

    @GetMapping("/invoice")
    public void invoice(HttpServletResponse response, @RequestParam(name = "id") Long id) throws DocumentException, IOException {
        Paiement paiement = paymentService.getPaymentById(id);
        Context context = new Context();

        Gym gym = gymService.getById(1L);

        context.setVariable("gym",gym);
        String imageFilename = gym.getLogo();
        imageFilename = imageFilename.trim();
        // Convert the image to base64 and add it to the context
        Path imagePath = Paths.get( "uploads", imageFilename);
        String base64Image = convertToBase64(imagePath);
        context.setVariable("base64Image", base64Image);
        context.setVariable("payment", paiement);
        System.out.println(gym.getLogo());

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
        response.setHeader("Content-Disposition", "attachment; filename=file.pdf");

        // Write the PDF content to the response output stream
        outputStream.writeTo(response.getOutputStream());
        outputStream.flush();
        outputStream.close();

    }

    public String convertToBase64(Path imagePath) throws IOException {
        byte[] imageBytes = Files.readAllBytes(imagePath);
        return Base64.getEncoder().encodeToString(imageBytes);
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


}
