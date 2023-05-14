package com.sportclub.sportclub.service;

import com.sportclub.sportclub.entities.Member;
import com.sportclub.sportclub.entities.Paiement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PaymentService {
    void addPayement(Paiement paiement);


    void deletePayment(Long id);

    Paiement getPaymentById(Long id);
    void updatePayment(Paiement m);
    List<Paiement> getAllPayment();
}
