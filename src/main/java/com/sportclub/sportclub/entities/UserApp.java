package com.sportclub.sportclub.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
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

public class UserApp extends Person {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "user_generator")
    @TableGenerator(name = "user_generator", allocationSize = 1)
    private Long id;

    public UserApp(Long id, String email, String password, Role roles) {
        this.id = id;
        this.roles = roles;
        this.email = email;
        this.password = password;
    }

    public UserApp(String email, String password, Role roles) {
        this.roles = roles;
        this.email = email;
        this.password = password;
    }
    public UserApp(String email, String password) {

        this.email = email;
        this.password = password;
    }



    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                '}';
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    private Role roles;
    @ManyToMany(mappedBy = "recipient")
    private List<Notification> notifications;
    @Column(unique = true)
    private String email;

    public UserApp(String pic, String name, String lname, String adress, String cin, LocalDate dob, String tele, String email, String password) {
        super(pic, name, lname, adress, cin, dob, tele);
        this.email = email;
        this.password = password;
    }

    private String password;
    public UserApp( String name, String lname, String adress, String cin, LocalDate dob, String tele, String email, String password) {
        super(name, lname, adress, cin, dob, tele);
//        this.id = id;

        this.email = email;
        this.password = password;
    }
    public UserApp(String name, String lname, String adress, String cin, LocalDate dob, String tele, Role roles, String email, String password) {
        super(name, lname, adress, cin, dob, tele);
        this.roles = roles;
        this.email = email;
        this.password = password;
    }
    @Builder
    public UserApp(String pic,String name, String lname, String adress, String cin, LocalDate dob, String tele, Role roles, String email, String password) {
        super(pic,name, lname, adress, cin, dob, tele);
        this.roles = roles;
        this.email = email;
        this.password = password;
    }
}
