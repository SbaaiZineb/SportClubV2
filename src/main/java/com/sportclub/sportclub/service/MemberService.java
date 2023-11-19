package com.sportclub.sportclub.service;

import com.sportclub.sportclub.entities.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface MemberService {
     void addMember(Member member);
List<Member> getMemberByMembership(Long abid);
     List<Member> getMemberBynName(String name);

    void deletMember(Long id);
    Page<Member> findByMemberName(String mc, Pageable pageable);
     Member getMemberById(Long id);
     void updateMember(Member m);
     List<Member> getAllMembers();
     long count();
     Boolean checkEmail(String email);

     public void ifPicIsEmpty(List<UserApp> userAppList, List<Member> members, List<Coach> coaches);
     Boolean checkCinExist(String cin);
     List<Member> getByCin(String cin);
}
