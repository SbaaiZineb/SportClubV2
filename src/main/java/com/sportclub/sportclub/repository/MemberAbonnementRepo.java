package com.sportclub.sportclub.repository;

import com.sportclub.sportclub.entities.Abonnement;
import com.sportclub.sportclub.entities.Member;
import com.sportclub.sportclub.entities.MemberAbonnement;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface MemberAbonnementRepo extends JpaRepository<MemberAbonnement,Long> {

    MemberAbonnement findByMemberAndAbonnementAndBookedDate(Member member,Abonnement abonnement, LocalDate bookedDate);
    List<MemberAbonnement> findByMember(Member member, Sort sort);

    @Query("SELECT ma FROM MemberAbonnement ma WHERE YEAR(ma.bookedDate) = :year")
    List<MemberAbonnement> findByBookedDateYear(@Param("year") int year);



}
