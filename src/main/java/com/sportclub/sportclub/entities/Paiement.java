package com.sportclub.sportclub.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;
    @OneToOne
    private Abonnement abonnement;
}
