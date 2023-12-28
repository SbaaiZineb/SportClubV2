package com.sportclub.sportclub.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data@AllArgsConstructor@NoArgsConstructor
public class Paiement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate start_date;
    private LocalDate end_date;
    private String status;
    private String payedBy;
    private double totalAmount;
    private double amountPaid;

    @OneToMany(mappedBy = "paiement", cascade = CascadeType.ALL)
    private List<Cheque> cheques;

    @JsonFormat(pattern = "YYYY-MM-dd")
    private LocalDate payedAt;
    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;
    @OneToOne
    private Abonnement abonnement;
}
