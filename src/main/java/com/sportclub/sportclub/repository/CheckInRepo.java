package com.sportclub.sportclub.repository;

import com.sportclub.sportclub.entities.CheckIn;
import com.sportclub.sportclub.entities.Member;
import com.sportclub.sportclub.entities.Seance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CheckInRepo extends JpaRepository<CheckIn ,Long> {
    List<CheckIn> findBySession(Seance seance);
List<CheckIn> getCheckInByCheckinDate(LocalDate localDate);
List<CheckIn> getCheckInByMember(Member member);
List<CheckIn> findByMemberAndAndCheckinDateBetween(Member member,LocalDate sDate,LocalDate eDate);
}
