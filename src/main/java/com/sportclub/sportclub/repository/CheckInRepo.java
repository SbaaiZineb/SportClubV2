package com.sportclub.sportclub.repository;

import com.sportclub.sportclub.entities.CheckIn;
import com.sportclub.sportclub.entities.Member;
import com.sportclub.sportclub.entities.Seance;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CheckInRepo extends JpaRepository<CheckIn, Long> {
    List<CheckIn> findBySession(Seance seance);

    List<CheckIn> findByMember(Member member);

    List<CheckIn> findByMemberRolesRoleName(String role);

    List<CheckIn> findByMemberAndCheckinDateBetween(Member member, LocalDate sDate, LocalDate eDate);

    List<CheckIn> findByCheckinDateBetween(LocalDate stratWeek, LocalDate endWeek);

    List<CheckIn> getCheckInByCheckinDate(LocalDate localDate, Sort sort);

    List<CheckIn> getCheckInByMember(Member member);
    @Modifying
    @Transactional
    @Query("DELETE FROM CheckIn c WHERE c IN :checkIns")
    void deleteAllCheck(@Param("checkIns") List<CheckIn> checkIns);

    List<CheckIn> findByMemberAndSessionAndCheckinDateBetween(Member member, Seance seance, LocalDate sDate, LocalDate eDate);
}
