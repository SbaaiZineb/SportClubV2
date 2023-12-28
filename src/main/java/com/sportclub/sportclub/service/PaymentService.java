package com.sportclub.sportclub.service;

import com.sportclub.sportclub.entities.Abonnement;
import com.sportclub.sportclub.entities.Member;
import com.sportclub.sportclub.entities.Paiement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PaymentService {
    void addPayement(Paiement paiement);

    Paiement findByMember(Long id);

    void deletePayment(Long id);

    List<Paiement> getByMemberPhone(String kw);

    Page<Paiement> getPage(String mc, Pageable pageable);

    Paiement getPaymentById(Long id);

    void updatePayment(Paiement m);

    List<Paiement> getAllPayment();

    List<Paiement> getPaymentsByMember(Member member);

    List<Paiement> getPaymentsByAbo(Abonnement abonnement);
}
