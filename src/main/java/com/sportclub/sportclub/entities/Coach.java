package com.sportclub.sportclub.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Coach extends User {
    private String sport_type;
    @OneToMany(mappedBy = "coach",fetch = FetchType.EAGER)
    private List<Groupe> groupes;

    public Coach(String name, String lname, String adress, String cin, LocalDate dob, int tele, List<Role> roles, String email, String password, String sport_type) {
        super(name, lname, adress, cin, dob, tele, roles, email, password);
        this.sport_type = sport_type;
    }

    @OneToOne(mappedBy = "coach",fetch = FetchType.LAZY)
    private Seance classe;

}
