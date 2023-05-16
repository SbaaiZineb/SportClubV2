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
public class Coach extends User {
    private String sport_type;
    @OneToMany(mappedBy = "coach",fetch = FetchType.EAGER)
    private List<Groupe> groupes;

    public Coach(String name, String lname, String adress, String cin, LocalDate dob, int tele, List<Role> roles, String email, String password, String sport_type) {
        super(name, lname, adress, cin, dob, tele, roles, email, password);
        this.sport_type = sport_type;
    }

    @OneToMany(mappedBy = "coach",cascade = { CascadeType.ALL}, fetch = FetchType.LAZY )
    private List<Seance> seances;

    @Override
    public String toString() {
        return getName()+" "+getLname();
    }
}
