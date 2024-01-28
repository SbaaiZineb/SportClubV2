package com.sportclub.sportclub.repository;

import com.sportclub.sportclub.entities.Abonnement;
import com.sportclub.sportclub.entities.Member;
import com.sportclub.sportclub.entities.Paiement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface PaymentRepo extends JpaRepository<Paiement,Long> {
Paiement findByMember(Member member);
List<Paiement> findPaiementByMember(Member member, Sort sort);
List<Paiement> findByMemberTeleContains(String kw);
List<Paiement> findByMemberTeleContainsOrMemberCinContainsIgnoreCase(String kw1,String kw2);
Page<Paiement> findByMemberNameContains(String kw,Pageable pageable);
List<Paiement> getPaiementByMember(Member member);
List<Paiement> getPaiementByAbonnement(Abonnement abonnement);
//List<Paiement> findByStart_dateBetween(LocalDate startDate,LocalDate endDate);
    @Query("SELECT p FROM Paiement p WHERE YEAR(p.payedAt) = :year")
List<Paiement> findByPayedAtYear(@Param("year") int year);

}
