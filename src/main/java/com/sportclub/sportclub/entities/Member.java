package com.sportclub.sportclub.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.annotation.Nullable;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor

public class Member extends UserApp {
    private String gender;
    private String status;

    private String profession;
    private String famSituation;

    private String secondTele;

    private String health;

    public Member(String pic, String name, String lname, String adress, String cin, LocalDate dob, String tele, String email, String password, String gender) {
        super(pic, name, lname, adress, cin, dob, tele, email, password);
        this.gender = gender;
    }

    public Member(String pic, String name, String lname, String adress, String cin, LocalDate dob, String tele, String email, String password, String gender, LocalDate createdAt) {
        super(pic, name, lname, adress, cin, dob, tele, email, password);
        this.gender = gender;
        this.createdAt = createdAt;
    }



    @Override
    public String toString() {
        return getName() + " " + getLname();
    }

    public Member(String name, String lname, String adress, String cin, LocalDate dob, String tele, Role roles, String email, String password, String gender) {
        super(name, lname, adress, cin, dob, tele, roles, email, password);
        this.gender = gender;
    }
    @Lob
    private String notes;

    @Temporal(value = TemporalType.DATE)
    private LocalDate createdAt;
    @OneToMany(mappedBy = "member", fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private List<Paiement> paiement;

   /* @ManyToMany(mappedBy = "members", cascade = CascadeType.ALL)
    private List<Seance> seanceList;*/
    @OneToMany(mappedBy = "member")
    private List<MemberAbonnement> memberAbonnements;
    @OneToMany(mappedBy = "member",cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private List<CheckIn> checkIn;


    public MemberAbonnement getCurrentAbonnement() {
        MemberAbonnement currentAbonnement = null;

        for (MemberAbonnement memberAbonnement : memberAbonnements) {

            if ("Active".equals(memberAbonnement.getAbStatus())){
                currentAbonnement = memberAbonnement;
//                break; // stop the loop once an "Active" status is found
            }
        }

        return currentAbonnement;
    }
}
