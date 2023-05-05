package com.sportclub.sportclub.service;

import com.sportclub.sportclub.entities.Abonnement;
import com.sportclub.sportclub.entities.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface MemberService {
     void addMember(Member member);

     List<Member> getMemberBynName(String name);
    void deletMember(Long id);
    Page<Member> findByMemberName(String mc, Pageable pageable);
     Member getMemberById(Long id);
     void updateMember(Member m);
     List<Member> getAllMembers();
}
