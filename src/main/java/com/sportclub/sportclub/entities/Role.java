package com.sportclub.sportclub.entities;

import jakarta.persistence.*;

import java.util.List;
@Entity
public class Role {
    @Id@GeneratedValue(strategy = GenerationType.AUTO)
    private Long role_id;
    private String role_name;
    @ManyToMany(mappedBy = "roles")
    private List<User> user;
}
