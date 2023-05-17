package com.sportclub.sportclub.service;

import com.sportclub.sportclub.entities.Abonnement;
import com.sportclub.sportclub.entities.Member;

import com.sportclub.sportclub.repository.AbonnementRepo;
import com.sportclub.sportclub.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MemberServiceImp implements MemberService {

    @Autowired
    MemberRepository memberRepository;

@Autowired
AbonnementRepo abonnementRepo;
    @Override
    public void addMember(Member member) {
        memberRepository.save(member);
    }

    @Override
    public List<Member> getMemberByMembership(Long abId) {
        Abonnement membership = abonnementRepo.findById(abId).get();
        return memberRepository.findByAbonnement(membership);
    }


    @Override
    public List<Member> getMemberBynName(String name) {
        return memberRepository.findByNameContains(name);
    }
    @Override
    public Page<Member> findByMemberName(String mc, Pageable pageable) {
        return memberRepository.findByNameContains(mc,pageable);}
    @Override
    public void deletMember(Long id) {
        boolean exists = memberRepository.existsById(id);
        if (!exists){
            throw new IllegalStateException("member with id "+id+" does not exists");
        }
        memberRepository.deleteById(id);

    }

    @Override
    public Member getMemberById(Long id) {
        return memberRepository.findById(id).get();
    }

    @Override
    public void updateMember(Member m) {
        memberRepository.save(m);
    }


    @Override
    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }
}