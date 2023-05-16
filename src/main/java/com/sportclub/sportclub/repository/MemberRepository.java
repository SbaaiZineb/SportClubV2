package com.sportclub.sportclub.repository;

import com.sportclub.sportclub.entities.Abonnement;
import com.sportclub.sportclub.entities.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Page<Member> findByNameContains(String mc, Pageable pageable);
    List<Member> findByNameContains(String s);
    List<Member> findByAbonnement(Abonnement membership);
}
