package com.sportclub.sportclub.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Entity
@Data@AllArgsConstructor@NoArgsConstructor
public class Paiement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate start_date;
    private LocalDate end_date;
    private String statue;
    @OneToOne
    private Member member;
    @OneToOne
    private Abonnement abonnement;
}
