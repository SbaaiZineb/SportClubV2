package com.sportclub.sportclub.service;

import com.sportclub.sportclub.entities.*;
import com.sportclub.sportclub.repository.ChequeRepo;
import com.sportclub.sportclub.repository.MemberAbonnementRepo;
import com.sportclub.sportclub.repository.MemberRepository;
import com.sportclub.sportclub.repository.PaymentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.File;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PaymentServiceImp implements PaymentService{

    @Autowired
    PaymentRepo paymentRepo;
    @Autowired
    MemberRepository memberRepository;
    @Override
    public void addPayement(Paiement paiement) {
        paymentRepo.save(paiement);
    }

    @Override
    public Paiement findByMember(Long id) {
        Member member=memberRepository.findById(id).get();
        return paymentRepo.findByMember(member);
    }


    @Override
    public void deletePayment(Long id) {
paymentRepo.deleteById(id);
    }

    @Override
    public List<Paiement> getByMemberPhone(String kw) {
        return paymentRepo.findByMemberTeleContains(kw);
    }

    @Override
    public Page<Paiement> getPage(String kw,Pageable pageable) {
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.DESC, "id"));

        return paymentRepo.findByMemberNameContains(kw, pageable);
    }


    @Override
    public Paiement getPaymentById(Long id) {
        return paymentRepo.findById(id).get();
    }

    @Override
    public void updatePayment(Paiement m) {
paymentRepo.save(m);
    }

    @Override
    public List<Paiement> getAllPayment() {
        return paymentRepo.findAll();
    }

    @Override
    public List<Paiement> getPaymentsByMember(Member member) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        return paymentRepo.findPaiementByMember(member,sort);
    }

    @Override
    public List<Paiement> getPaymentsByAbo(Abonnement abonnement) {
        return paymentRepo.getPaiementByAbonnement(abonnement);
    }

    @Override
    public List<Paiement> getMonthlyRevenue(LocalDate startDate, LocalDate endDate) {
        return paymentRepo.findByPayedAtBetween(startDate,endDate);
    }


}
