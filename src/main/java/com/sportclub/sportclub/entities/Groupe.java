package com.sportclub.sportclub.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Entity
@Data@AllArgsConstructor@NoArgsConstructor
public class Groupe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String g_name;
    private int nbr_member;
    @ManyToOne
    private Coach coach;
    @ManyToMany(mappedBy = "groupes")
    private List<Member> g_members;
}
