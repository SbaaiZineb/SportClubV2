package com.sportclub.sportclub.repository;

import com.sportclub.sportclub.entities.Member;
import com.sportclub.sportclub.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Page<Member> findByNameContains(String mc, Pageable pageable);
    List<Member> findByName(String s);
}
