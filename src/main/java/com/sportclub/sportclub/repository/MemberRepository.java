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
    @Query("SELECT m, COUNT(u) FROM Member  u JOIN u.abonnement m GROUP BY m")
    List<Object[]> countUsersByMembership();
    long countByAbonnement(Abonnement abonnement);
    Page<Member> findByNameContains(String mc, Pageable pageable);
    List<Member> findByLnameContains(String s);
    List<Member> findByAbonnement(Abonnement membership);
    @Query("select count(p) = 1 from Member p where p.email= ?1")
    Boolean findExistByEmail(String email);
    List<Member> findByCinContains(String cin);
}
