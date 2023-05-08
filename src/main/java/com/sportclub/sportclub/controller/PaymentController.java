package com.sportclub.sportclub.controller;

import com.sportclub.sportclub.entities.Abonnement;
import com.sportclub.sportclub.entities.Member;
import com.sportclub.sportclub.entities.Paiement;
import com.sportclub.sportclub.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class PaymentController {
    @Autowired
    PaymentService paymentService;
    @GetMapping("/paymentList")
    public String getPayment(Model model , @RequestParam(name = "page",defaultValue = "0") int page,
                         @RequestParam(name = "size",defaultValue = "5") int size,
                         @RequestParam(name = "keyword",defaultValue = "") String kw
    ) {

//        Page<Paiement> pageP = paymentService.getPaymentById(kw, PageRequest.of(page,size));
//        model.addAttribute("listPayment",pageP.getContent());
//        model.addAttribute("pages",new int[pageP.getTotalPages()]);
//        model.addAttribute("currentPage",page);
//        model.addAttribute("keyword",kw);

List<Paiement> paiements=paymentService.getAllPayment();
model.addAttribute("paymentList",paiements);
        Paiement paiement = new Paiement();
        model.addAttribute("payment", paiement);
        return "paymentList";

    }







}
