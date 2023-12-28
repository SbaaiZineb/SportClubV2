package com.sportclub.sportclub.service;

import com.sportclub.sportclub.entities.Abonnement;
import com.sportclub.sportclub.entities.Member;
import com.sportclub.sportclub.entities.Paiement;
import com.sportclub.sportclub.repository.MemberRepository;
import com.sportclub.sportclub.repository.PaymentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
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
        return paymentRepo.getPaiementByMember(member);
    }

    @Override
    public List<Paiement> getPaymentsByAbo(Abonnement abonnement) {
        return paymentRepo.getPaiementByAbonnement(abonnement);
    }
}
