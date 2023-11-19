package com.sportclub.sportclub.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@SQLDelete(sql = "UPDATE Abonnement SET deleted = true WHERE id=?")
@Where(clause = "deleted=false")
public class Abonnement {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String nameAb;
    private double price;
    private int nbrSeance;
    private boolean deleted = Boolean.FALSE;

    public Abonnement(String nameAb, double price, String period,int nbrSeance) {
        this.nameAb = nameAb;
        this.price = price;
        this.period = period;
        this.nbrSeance=nbrSeance;
    }

    private String period;
    @OneToMany(mappedBy = "abonnement",fetch = FetchType.EAGER)
    private List<Member> members;




    @Override
    public String toString() {
        return nameAb;
    }
}
