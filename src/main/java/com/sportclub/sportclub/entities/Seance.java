package com.sportclub.sportclub.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
@Entity
@Data@AllArgsConstructor@NoArgsConstructor
public class Seance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String className;
    @Temporal(value = TemporalType.TIMESTAMP)
    private LocalDateTime start_time;
    @Temporal(value = TemporalType.TIMESTAMP)
    private LocalDateTime end_time;

    @ManyToMany
    private List<Member> members;
    @OneToOne
    private Coach coach;
}
