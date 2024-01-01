package com.sportclub.sportclub.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class MemberAbonnement {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "abonnement_id")
    private Abonnement abonnement;
    private LocalDate bookedDate;


    private String abStatus;

}
