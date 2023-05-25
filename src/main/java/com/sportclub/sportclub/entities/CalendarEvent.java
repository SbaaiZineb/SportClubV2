package com.sportclub.sportclub.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@Entity
@AllArgsConstructor@NoArgsConstructor
public class CalendarEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    @JsonFormat(pattern = "YYYY-MM-dd")
    private LocalDate start;
    @JsonFormat(pattern = "YYYY-MM-dd")
    private LocalDate startRecur;
    @JsonFormat(pattern = "YYYY-MM-dd")
    private LocalDate endRecur;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;
    @ElementCollection
    @Column(name = "daysOfWeek")
    private List<Integer> daysOfWeek;
    private String username;
}
