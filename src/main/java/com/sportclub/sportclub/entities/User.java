package com.sportclub.sportclub.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.service.spi.InjectService;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Data
@AllArgsConstructor
@NoArgsConstructor

public abstract class User extends Person {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                '}';
    }

    @ManyToMany
    private List<Role> roles;
    private String email;

    public User(String pic, String name, String Lname, String adress, String cin, LocalDate dob, int tele, String email, String password) {
        super(pic, name, Lname, adress, cin, dob, tele);
        this.email = email;
        this.password = password;
    }

    private String password;

    public User( String name, String Lname, String adress, String cin, LocalDate dob, int tele, String email, String password) {
        super(name, Lname, adress, cin, dob, tele);
//        this.id = id;

        this.email = email;
        this.password = password;
    }

    public User(String name, String lname, String adress, String cin, LocalDate dob, int tele, List<Role> roles, String email, String password) {
        super(name, lname, adress, cin, dob, tele);
        this.roles = roles;
        this.email = email;
        this.password = password;
    }
}
