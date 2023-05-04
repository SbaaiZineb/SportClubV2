package com.sportclub.sportclub.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

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

    @Override
    public String toString() {
        return "Seance{" +
                "id=" + id +
                ", className='" + className + '\'' +
                ", start_time=" + start_time +
                ", end_time=" + end_time +
                ", coach=" + coach +
                '}';
    }

    @Temporal(value = TemporalType.TIMESTAMP)
    private LocalDateTime start_time;
    @Temporal(value = TemporalType.TIMESTAMP)
    private LocalDateTime end_time;

    @ManyToMany
    private List<Member> members;
    @ManyToOne
    @JoinColumn(name = "coach_id")
    private Coach coach;
}
