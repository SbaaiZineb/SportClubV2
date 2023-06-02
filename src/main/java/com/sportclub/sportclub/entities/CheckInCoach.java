package com.sportclub.sportclub.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
@Entity
@Getter
@Setter
@AllArgsConstructor@NoArgsConstructor
public class CheckInCoach {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "coach_id")
    private Coach coach;

    @ManyToOne
    @JoinColumn(name = "session_id")
    private Seance session;

    private LocalDate checkinDate;
    private LocalTime checkinTime;
    private String statut="Checkedin";

}
