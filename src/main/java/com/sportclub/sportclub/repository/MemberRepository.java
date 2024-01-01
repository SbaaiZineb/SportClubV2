package com.sportclub.sportclub.repository;

import com.sportclub.sportclub.entities.Abonnement;
import com.sportclub.sportclub.entities.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    @Query("SELECT m, COUNT(u) FROM Member u JOIN u.memberAbonnements ma JOIN ma.abonnement m GROUP BY m")
    List<Object[]> countUsersByMembership();

    long countByMemberAbonnements_Abonnement(Abonnement abonnement);
    Page<Member> findByNameContains(String mc, Pageable pageable);
    List<Member> findByLnameContainsIgnoreCase(String s);
    List<Member> findByCinContainsIgnoreCase(String cin);

    List<Member> findByTeleContains(String tele);
    List<Member> findByMemberAbonnements_Abonnement(Abonnement membership);
    @Query("select count(p) = 1 from Member p where p.email= ?1")
    Boolean findExistByEmail(String email);

    @Query("select count(p) = 1 from Member p where p.cin= ?1")
    Boolean findExistByCin(String cin);
}
