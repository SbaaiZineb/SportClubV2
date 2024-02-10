package com.sportclub.sportclub.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cheque {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    private String namePayor;
    private int numCheque;
    private double chequeMontant;
    private LocalDate chequeDate;
    @ManyToOne
    @JoinColumn(name = "paiement_id")
    private Paiement paiement;
    @Override
    public String toString() {
        return "Cheque{" +
                "id=" + id +
                ", numCheque='" + numCheque + '\'' +
                ", chequeMontant=" + chequeMontant +
                ", chequeDate=" + chequeDate +
                ", namePayor='" + namePayor + '\'' +
                // Include other fields here, excluding any collections that might cause cyclic references
                '}';
    }
}
