package com.sportclub.sportclub.service;

import com.sportclub.sportclub.entities.Member;
import com.sportclub.sportclub.entities.Paiement;
import com.sportclub.sportclub.repository.PaymentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class PaymentServiceImp implements PaymentService{
    @Autowired
    PaymentRepo paymentRepo;
    @Override
    public void addPayement(Paiement paiement) {
        paymentRepo.save(paiement);
    }



    @Override
    public void deletePayment(Long id) {
paymentRepo.deleteById(id);
    }



    @Override
    public Paiement getPaymentById(Long id) {
        return paymentRepo.findById(id).get();
    }

    @Override
    public void updatePayment(Paiement m) {

    }

    @Override
    public List<Paiement> getAllPayment() {
        return paymentRepo.findAll();
    }
}
