package com.sportclub.sportclub.controller;

import com.lowagie.text.DocumentException;
import com.sportclub.sportclub.entities.*;
import com.sportclub.sportclub.repository.ChequeRepo;
import com.sportclub.sportclub.repository.MemberAbonnementRepo;
import com.sportclub.sportclub.repository.PaymentRepo;
import com.sportclub.sportclub.service.*;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
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
@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('EMPLOYEE')")
public class PaymentController {
    @Autowired
    PaymentService paymentService;
    @Autowired
    AdminService adminService;
    @Autowired
    PaymentRepo paymentRepo;
    @Autowired
    ChequeRepo chequeRepo;
    @Autowired
    MemberService memberService;
    @Autowired
    GymService gymService;
    @Autowired
    AbonnementService abonnementService;
    @Autowired
    MemberAbonnementRepo memberAbonnementRepo;
    @Value("${app.report.location}")
    private String DIRECTORY;
    private static final String DEFAULT_FILE_NAME = "totalprintreport.pdf";
    @Autowired
    private ServletContext servletContext;

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

    @GetMapping("/payments/search")
    public String search(@RequestParam("keyword") String keyword, Model model) {
        model.addAttribute("payment", new Paiement());
        List<Paiement> searchResults;

        if (!keyword.isEmpty()) {
            searchResults = paymentRepo.findByMemberTeleContainsOrMemberCinContainsIgnoreCase(keyword,keyword);
            System.out.println("Result : " +searchResults);
        } else {
            return "redirect:/payments";
        }
        model.addAttribute("paymentList", searchResults);
        model.addAttribute("keyword", keyword);

        return "paymentList";
    }

    @GetMapping("payments/pay")
    public String getPay(@RequestParam(name = "id") Long id, Model model) {
        LocalDate localDate = LocalDate.now();
        model.addAttribute("date", localDate);
        Paiement paiement = paymentService.getPaymentById(id);
        model.addAttribute("payment", paiement);
        List<Cheque> cheques = new ArrayList<>();
        cheques.add(new Cheque());


        model.addAttribute("cheques", cheques); // Add cheques to the model
        model.addAttribute("cheque", new Cheque());
        return "paymentModal";
    }


    @PostMapping("/payments/pay")
    public String pay(@Validated Paiement paiement, BindingResult bindingResult, Authentication authentication,
                      RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) return "error";

        if (paiement.getPayedBy().equals("Cheque")) {
            for (Cheque cheque : paiement.getCheques()) {
                if (cheque != null) {

                    cheque.setNamePayor(paiement.getMember().getName() + " " + paiement.getMember().getLname());
                    cheque.setPaiement(paiement);

                    System.out.println("Cheque Number: " + cheque.getNumCheque());
                    System.out.println("Cheque Amount: " + cheque.getChequeMontant());
                    System.out.println("Cheque Date: " + cheque.getChequeDate());
                    System.out.println("----------------------");

                    chequeRepo.save(cheque);
                }

            }
        }

        UserApp user = adminService.loadUserByUsername(authentication.getName());
        String userRole = user.getRoles().getRoleName();

        paiement.setPayedAt(LocalDate.now());
        paiement.setStatus("Payé");
        paymentService.updatePayment(paiement);

        MemberAbonnement memberMembership = memberAbonnementRepo.findByPaiement(paiement);
        System.out.println(memberMembership + "!!!!!!!!!");


        if (memberMembership != null) {
            LocalDate currentDate = LocalDate.now();
            LocalDate startDate = memberMembership.getStartDate();
            LocalDate endDate = memberMembership.getEndDate();

            boolean isWithinValidPeriod = endDate != null && (currentDate.isEqual(startDate) ||
                    (currentDate.isAfter(startDate) && currentDate.isBefore(endDate) || currentDate.isEqual(endDate)));

            boolean isUnlimitedMembership = endDate == null &&
                    (startDate.isBefore(currentDate) || startDate.isEqual(currentDate)) &&
                    memberMembership.getNbrSessionCarnet() >= 0;
            // Update memberMembership status
            if (isWithinValidPeriod || isUnlimitedMembership) {

                memberMembership.setAbStatus("Active");
                System.out.println("Active!!!!!!!!"+memberMembership);

            } else if (startDate.isAfter(currentDate) && memberMembership.getNbrSessionCarnet() >= 0){
                memberMembership.setAbStatus("Programmé");
                System.out.println("Program!!!!!!!!!!"+memberMembership);
            }

            memberService.updateMemberAbonnement(memberMembership);

        }
        redirectAttributes.addFlashAttribute("successMessage", "Paiement effectué avec succès!");

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
        if (imageFilename != null && !imageFilename.isEmpty()) {

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

    @GetMapping("/generateMonthlyReport")
    public void generateReport(@RequestParam(name = "fromDate") LocalDate startDate,
                               @RequestParam(name = "toDate") LocalDate endDate,HttpServletResponse response) throws DocumentException, IOException {
        List<Paiement> paiements = paymentService.getMonthlyRevenue(startDate,endDate);
        System.out.println("Start Date :"+startDate);
        System.out.println("End Date : "+endDate);
        System.out.println(paiements);
        double total = 0;
        for (Paiement payment:paiements
             ) {
             double price = payment.getMontant();
             total +=price;
        }
        Context context = new Context();
        context.setVariable("total",total);
        context.setVariable("fromDate",startDate);
        context.setVariable("toDate",endDate);

        context.setVariable("payments", paiements);

        String htmlContent = templateEngine.process("report", context);

        // Create a ByteArrayOutputStream to hold the PDF content
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // Use Flying Saucer to convert HTML to PDF
        ITextRenderer renderer = new ITextRenderer();

        renderer.setDocumentFromString(htmlContent);
        renderer.layout();
        renderer.createPDF(outputStream);

        // Set the response headers for PDF download
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=rapport.pdf");

        // Write the PDF content to the response output stream
        outputStream.writeTo(response.getOutputStream());
        outputStream.flush();
        outputStream.close();

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

    public String cancelPayment(@RequestParam(name = "payId") Long id, Authentication authentication) {
        UserApp user = adminService.loadUserByUsername(authentication.getName());
        String userRole = user.getRoles().getRoleName();

        try {

            Paiement paiement = paymentService.getPaymentById(id);
            paiement.setStatus("Annulée");
            paymentService.updatePayment(paiement);


            MemberAbonnement memberAbonnement = memberAbonnementRepo.findByPaiement(paiement);


            if (memberAbonnement != null) {

                // Update MemberAbonnement status
                memberAbonnement.setAbStatus("Annulé");
                memberAbonnementRepo.save(memberAbonnement);
            }


        } catch (Exception e) {
            System.out.println("Something is wrong !! " + e);
        }
        if (userRole.equals("EMPLOYEE")) {
            return "redirect:/employee/payments";
        }
        return "redirect:/payments";
    }


    @GetMapping("/paymentDetails")
    public String paymentDetails(@RequestParam(name = "id") Long paymentId, Model model) {

        Paiement payment = paymentService.getPaymentById(paymentId);
        model.addAttribute("payment", payment);

        return "paymentDetails";
    }
}
