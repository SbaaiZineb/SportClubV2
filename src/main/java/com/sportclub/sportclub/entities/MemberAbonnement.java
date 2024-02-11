package com.sportclub.sportclub.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
@Data
public class MemberAbonnement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "abonnement_id")
    private Abonnement abonnement;
    private LocalDate bookedDate;


    private String abStatus;
    private LocalDate startDate;
    private LocalDate endDate;
    private double montant;
    @OneToOne
    private Paiement paiement;
    private int nbrSessionCarnet;

    public Long daysUntilExpiration(LocalDate startDate, LocalDate endDate) {
        if (endDate == null || startDate == null) {
            return null;
        }
        return ChronoUnit.DAYS.between(startDate, endDate);
    }

}
