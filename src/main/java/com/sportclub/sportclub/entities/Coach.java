package com.sportclub.sportclub.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Coach extends UserApp {
    private String sportType;


    public Coach(String name, String lname, String adress, String cin, LocalDate dob, String tele, Role roles, String email, String password, String sportType) {
        super(name, lname, adress, cin, dob, tele, roles, email, password);
        this.sportType = sportType;
    }

    @OneToMany(mappedBy = "coach", fetch = FetchType.LAZY, cascade = { CascadeType.MERGE, CascadeType.PERSIST })
    private List<CheckInCoach> checkInCoaches;
    @OneToMany(mappedBy = "coach",cascade = { CascadeType.ALL}, fetch = FetchType.LAZY )
    private List<Seance> seances;

    @Override
    public String toString() {
        return getName()+" "+getLname();
    }
}
