package com.sportclub.sportclub.entities;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@MappedSuperclass@Data@AllArgsConstructor@NoArgsConstructor

public class Person {
    private String pic;
    private String name;
    private String lname;

    public Person(String name, String lname, String adress, String cin, LocalDate dob, String tele) {
        this.name = name;
        this.lname = lname;
        this.adress = adress;
        this.cin = cin;
        this.dob = dob;
        this.tele = tele;
    }

    private String adress;
    private String cin;
    @Temporal(TemporalType.DATE)
    private LocalDate dob;

    private String tele;

}
