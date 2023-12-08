package com.sportclub.sportclub.service;

import com.sportclub.sportclub.controller.SetPayEndDate;
import com.sportclub.sportclub.entities.*;

import com.sportclub.sportclub.repository.AbonnementRepo;
import com.sportclub.sportclub.repository.AdminRepo;
import com.sportclub.sportclub.repository.MemberRepository;
import com.sportclub.sportclub.tools.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class MemberServiceImp implements MemberService {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    AbonnementRepo abonnementRepo;
    @Autowired
    AdminRepo adminRepo;
    @Autowired
    CheckInService checkInService;
    @Autowired
    PaymentService paymentService;
    @Autowired
    CoachService coachService;
    @Autowired
    PasswordEncoder passwordEncoder;
   @Autowired
    FileStorageService storageService;
    @Autowired
    NotificationService notificationService;
    @Autowired
    RoleService roleService;

    @Override
    public void addMember(Member memberForm, Authentication authentication, MultipartFile file) {
        LocalDate localDate = LocalDate.now();
        memberForm.setCreatedAt(localDate);

        if (!file.isEmpty()) {
            memberForm.setPic(file.getOriginalFilename());
            storageService.save(file);
        } else {
            memberForm.setPic("default-user.png");
        }

        memberForm.setStatue("Inactive");
        String password = memberForm.getPassword();
        memberForm.setPassword(passwordEncoder.encode(password));

        List<Role> roles = roleService.findAllRoles();

        for (Role role : roles) {
            if (role.getRoleName().equals("ADHERENT")) {
                memberForm.setRoles(role);
            }
        }

        memberRepository.save(memberForm);
        Notification notification = new Notification();
        String username = authentication.getName();
        notification.setTimestamp(LocalDateTime.now());
        notification.setMessage(username + " a ajouté un nouveau adherent");
        List<UserApp> userApps = adminRepo.findByRolesRoleNameContains("ADMIN");
        notification.setRecipient(userApps);
        notification.setTitle("Nouveau Adherent");
        notificationService.addNotification(notification);

        if (memberForm.getAbonnement() != null) {
            Paiement paiement = new Paiement();
            paiement.setStart_date(localDate);
            paiement.setStatue("Impayé");
            paiement.setAbonnement(memberForm.getAbonnement());
            String per = paiement.getAbonnement().getPeriod();

            SetPayEndDate sPD = new SetPayEndDate();
            sPD.setPayEndDate(per, paiement);
            paiement.setMember(memberForm);

            paymentService.addPayement(paiement);
        }
    }
    public long count() {
        return memberRepository.count();
    }

    @Override
    public Boolean checkEmail(String email) {
        return adminRepo.findExistByEmail(email);
    }

    @Override
    public Boolean checkCinExist(String cin) {
        return adminRepo.findExistByCin(cin);
    }

    @Override
    public List<Member> getByCin(String cin) {
        return memberRepository.findByCinContains(cin);
    }

    @Override
    public List<Member> getMemberByMembership(Long abId) {
        Abonnement membership = abonnementRepo.findById(abId).get();
        return memberRepository.findByAbonnement(membership);
    }


    @Override
    public List<Member> getMemberBynName(String name) {
        return memberRepository.findByLnameContains(name);
    }


    @Override
    public Page<Member> findByMemberName(String mc, Pageable pageable) {
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.DESC, "createdAt"));
        return memberRepository.findByNameContains(mc, pageable);
    }

    @Override
    public void deletMember(Long id) {
        boolean exists = memberRepository.existsById(id);
        if (!exists) {
            throw new IllegalStateException("member with id " + id + " does not exists");
        }
        Member member = memberRepository.findById(id).orElse(null);
        if (member != null) {
            List<CheckIn> checkIns = checkInService.getByMemberCheck(member);

            for (CheckIn checkIn : checkIns) {
                checkIn.setMember(null);
                checkInService.updateCheckIn(checkIn);
            }

            List<Paiement> paiementList = paymentService.getPaymentsByMember(member);
            for (Paiement payment : paiementList) {
                payment.setMember(null);
                paymentService.updatePayment(payment);
            }

            memberRepository.delete(member);
        }
    }




    @Override
    public Member getMemberById(Long id) {
        return memberRepository.findById(id).get();
    }

    @Override
    public void updateMember(Member m) {
        memberRepository.save(m);
    }

    public void ifPicIsEmpty(List<UserApp> userAppList, List<Member> members, List<Coach> coaches) {
        try{

            for (Member member : members
            ) {
                if (member.getPic()==null) {
                    member.setPic("default-user.png");
                    updateMember(member);
                }
            }
            for (Coach coach : coaches
            ) {
                if (coach.getPic()==null) {
                    coach.setPic("default-user.png");
                    coachService.updateCoach(coach);
                }
            } for (UserApp userApp : userAppList
            ) {
                if (userApp.getPic()==null) {
                    userApp.setPic("default-user.png");
                    adminRepo.save(userApp);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    @Override
    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }
}
