package com.sportclub.sportclub.repository;

import com.sportclub.sportclub.entities.*;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface CoachCheckInRepo extends JpaRepository<CheckInCoach,Long> {
    List<CheckInCoach> findBySession(Seance seance);
    List<CheckInCoach> findByCoachRolesRoleName(String role);
    List<CheckInCoach> getCheckInByCheckinDate(LocalDate localDate, Sort sort);
    List<CheckInCoach> getCheckInByCoach(Coach coach);
    List<CheckInCoach> findByCoachAndCheckinDateBetween(Coach coach,LocalDate sDate,LocalDate eDate);
}
