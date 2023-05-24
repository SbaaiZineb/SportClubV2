package com.sportclub.sportclub.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Entity
@Data@AllArgsConstructor@NoArgsConstructor
public class Role {
    @Id@GeneratedValue(strategy = GenerationType.AUTO)
    private Long role_id;
    private String roleName;
    @ManyToMany(mappedBy = "roles")
    private List<User> user;
}
