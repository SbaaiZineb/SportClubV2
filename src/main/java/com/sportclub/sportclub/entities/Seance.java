package com.sportclub.sportclub.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Data@AllArgsConstructor@NoArgsConstructor
public class Seance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String className;

    @Temporal(value = TemporalType.DATE)
    private LocalDate startDate;
    @Temporal(value = TemporalType.DATE)
    private int numWeeks;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;
//    @ManyToMany
//    private List<Member> members;
    @ManyToOne
    @JoinColumn(name = "coach_id")
    private Coach coach;
    @OneToMany(mappedBy = "session",  fetch = FetchType.EAGER, cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REMOVE })
    private List<CheckIn> checkIns;
    @OneToMany(mappedBy = "session",  fetch = FetchType.EAGER, cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REMOVE })
    private List<CheckInCoach> checkInCoaches;
    @ElementCollection(fetch = FetchType.EAGER)
    @Column(name = "days")
    private List<String> days=new ArrayList<>();

    public List<CheckIn> getCheckInsToday() {
        LocalDate today = LocalDate.now();
        return checkIns.stream()
                .filter(checkIn -> checkIn.getCheckinDate().isEqual(today))
                .collect(Collectors.toList());
    }
}
