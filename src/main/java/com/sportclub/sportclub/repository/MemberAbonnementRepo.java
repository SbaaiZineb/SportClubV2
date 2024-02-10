package com.sportclub.sportclub.repository;

import com.sportclub.sportclub.entities.Abonnement;
import com.sportclub.sportclub.entities.Member;
import com.sportclub.sportclub.entities.MemberAbonnement;
import com.sportclub.sportclub.entities.Paiement;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface MemberAbonnementRepo extends JpaRepository<MemberAbonnement, Long> {

    MemberAbonnement findByMemberAndAbonnementAndStartDate(Member member, Abonnement abonnement, LocalDate startDate);

    List<MemberAbonnement> findByMember(Member member, Sort sort);

    List<MemberAbonnement> findByMember(Member member);
    MemberAbonnement findByPaiement(Paiement payment);

    @Query("SELECT ma FROM MemberAbonnement ma WHERE YEAR(ma.bookedDate) = :year")
    List<MemberAbonnement> findByBookedDateYear(@Param("year") int year);


}
