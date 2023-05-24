package com.sportclub.sportclub.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Member extends User {
    private String gender;

    public Member(String pic, String name, String Lname, String adress, String cin, LocalDate dob, int tele, String email, String password, String gender) {
        super(pic, name, Lname, adress, cin, dob, tele, email, password);
        this.gender = gender;
    }
    public Member(String pic, String name, String Lname, String adress, String cin, LocalDate dob, int tele, String email, String password, String gender,LocalDate createdAt) {
        super(pic, name, Lname, adress, cin, dob, tele, email, password);
        this.gender = gender;
        this.createdAt=createdAt;
    }
    public Member(String name, String Lname, String adress, String cin, LocalDate dob, int tele, String email, String password, String gender) {
        super(name, Lname, adress, cin, dob, tele, email, password);
        this.gender = gender;

    }

    @Override
    public String toString() {
        return getName()+" "+getLname();
    }

    public Member(String name, String lname, String adress, String cin, LocalDate dob, int tele, List<Role> roles, String email, String password, String gender) {
        super(name, lname, adress, cin, dob, tele, roles, email, password);
        this.gender = gender;
    }
    @Temporal(value = TemporalType.DATE)
private LocalDate createdAt;
    @OneToMany(mappedBy = "member",  fetch = FetchType.EAGER, cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REMOVE })
    private List<Paiement> paiement;

    @ManyToMany
    private List<Seance> seanceList;
    @ManyToOne
    @JoinColumn(name = "ab_id")
    private Abonnement abonnement;
    @OneToMany(mappedBy = "member", fetch = FetchType.EAGER, cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REMOVE })
    private List<CheckIn> checkIn;
}
