package com.sportclub.sportclub.service;

import com.sportclub.sportclub.controller.SetPayEndDate;
import com.sportclub.sportclub.entities.*;

import com.sportclub.sportclub.repository.AbonnementRepo;
import com.sportclub.sportclub.repository.AdminRepo;
import com.sportclub.sportclub.repository.MemberAbonnementRepo;
import com.sportclub.sportclub.repository.MemberRepository;
import com.sportclub.sportclub.tools.FileStorageService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
public class MemberServiceImp implements MemberService {

    @Autowired
    MemberAbonnementRepo memberAbonnementRepo;
    @Autowired
    MemberRepository memberRepository;

    @Autowired
    AbonnementService abonnementService;
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
        public void addMember(Member memberForm, Authentication authentication, MultipartFile file, Long abonnementId,
                              LocalDate startDate,LocalDate endDate) {
            LocalDate localDate = LocalDate.now();
            memberForm.setCreatedAt(localDate);
    //Set pic to the default if pic is empty
            if (!file.isEmpty()) {
                memberForm.setPic(file.getOriginalFilename());
                storageService.save(file);
            } else {
                memberForm.setPic("default-user.png");
            }

            memberForm.setStatus("Inactive");
            String password = memberForm.getPassword();
            if (password != null) {
                memberForm.setPassword(passwordEncoder.encode(password));

            }

            List<Role> roles = roleService.findAllRoles();

            for (Role role : roles) {
                if (role.getRoleName().equals("ADHERENT")) {
                    memberForm.setRoles(role);
                }
            }
            memberRepository.save(memberForm);


            if (abonnementId != null) {
                MemberAbonnement memberAbonnement = new MemberAbonnement();
                Abonnement abonnement = abonnementService.getAboById(abonnementId);
                Paiement paiement = new Paiement();
                paiement.setStart_date(startDate);
                paiement.setStatus("Impayé");
                paiement.setAbonnement(abonnement);
                paiement.setMontant(paiement.getAbonnement().getPrice());
                paiement.setEnd_date(endDate);
                paiement.setMember(memberForm);

                paymentService.addPayement(paiement);

                memberAbonnement.setAbonnement(abonnement);
                memberAbonnement.setMontant(abonnement.getPrice());
                memberAbonnement.setBookedDate(localDate);
                memberAbonnement.setPaiement(paiement);
                memberAbonnement.setStartDate(startDate);
                memberAbonnement.setEndDate(endDate);
                memberAbonnement.setNbrSessionCarnet(abonnement.getNbrSeance());
                if (memberForm.getMemberAbonnements() == null) {
                    memberForm.setMemberAbonnements(new ArrayList<>());
                }
                memberAbonnement.setMember(memberForm);
                memberAbonnement.setAbStatus("En attente");


                // Save the MemberAbonnement
                memberAbonnementRepo.save(memberAbonnement);

                // Add MemberAbonnement to the member's list
                memberForm.getMemberAbonnements().add(memberAbonnement);

                // Save the updated member
                memberRepository.save(memberForm);

            }

            Notification notification = new Notification();
            String username = authentication.getName();
            notification.setTimestamp(LocalDateTime.now());
            notification.setMessage(username + " a ajouté un nouveau adherent");
            List<UserApp> userApps = adminRepo.findByRolesRoleNameContains("ADMIN");
            notification.setRecipient(userApps);
            notification.setTitle("Nouveau Adherent");
            notificationService.addNotification(notification);

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
    public List<Member> getMemberByCin(String cin) {
        return memberRepository.findByCinContainsIgnoreCase(cin);
    }

    @Override
    public List<Member> getMemberByMembership(Long abId) {
        Abonnement membership = abonnementService.getAboById(abId);
        return memberRepository.findByMemberAbonnements_Abonnement(membership);
    }


    @Override
    public List<Member> getMemberBynName(String name) {
        return memberRepository.findByLnameContainsIgnoreCase(name);
    }

    @Override
    public List<Member> getMemberByPhone(String phone) {
        return memberRepository.findByTeleContains(phone);
    }


    @Override
    public Page<Member> findByMemberName(String mc, Pageable pageable) {
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.DESC, "id"));
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
        try {

            for (Member member : members
            ) {
                if (member.getPic() == null) {
                    member.setPic("default-user.png");
                    updateMember(member);
                }
            }
            for (Coach coach : coaches
            ) {
                if (coach.getPic() == null) {
                    coach.setPic("default-user.png");
                    coachService.updateCoach(coach);
                }
            }
            for (UserApp userApp : userAppList
            ) {
                if (userApp.getPic() == null) {
                    userApp.setPic("default-user.png");
                    adminRepo.save(userApp);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    //Update the member's status
    public void updateMemberStatues() {
        try {

            List<Member> members = getAllMembers();

            for (Member member : members) {
                if (member != null) {

                    boolean hasActivePayment = memberHasActivePay(member);


                    if (!hasActivePayment) {
                        member.setStatus("Inactive");
                        System.out.println("!!!!!!!!!!!inactive!!!!!" + member.getName());
                    } else {
                        member.setStatus("Active");
                        System.out.println("!!!!!!!!!Active !!!!!!!!!!!!!!" + member.getName());
                    }

                    updateMember(member);
                }

            }
        } catch (Exception e) {
            System.out.println("Something is off " + e);
        }
    }

    public boolean memberHasActivePay(Member member) {
        List<MemberAbonnement> subscriptions = member.getMemberAbonnements();
        if (subscriptions != null && !subscriptions.isEmpty()) {

            for (MemberAbonnement sub : subscriptions) {
                if (sub.getAbStatus().equals("Active")) {
                    // If there is an active payment for the member
                    System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!Actiiiveee!!!!!!!!!!!!!" + sub.getStartDate() + " " + sub.getEndDate() + " " + member.getName());
                    return true;
                }
            }
        } else {
            // If there are no payments
            return false;
        }
        //If not
        return false;

    }

    public boolean isMembershipExpired(MemberAbonnement memberAb) {

        LocalDate currentDate = LocalDate.now();
        LocalDate expirationDate = memberAb.getEndDate();

        return (expirationDate != null && currentDate.isAfter(expirationDate)) || (expirationDate == null && memberAb.getNbrSessionCarnet() <= 0);
    }

    @Transactional
    public void updateMemberAbonnement(MemberAbonnement memberAbonnement) {
        memberAbonnementRepo.save(memberAbonnement);
    }
    @Override
    public List<Member> searchMembers(String query) {
        System.out.println(query+" it's the keyword !!!");
        return memberRepository.findByKeyword(query);
    }

    @Override
    public void updateMembershipStatus() {
        LocalDate currentDate = LocalDate.now();
        List<MemberAbonnement> memberAbonnementList = memberAbonnementRepo.findAll();

        for (MemberAbonnement memberAb : memberAbonnementList) {
            if (memberAb != null) {
                LocalDate startDate = memberAb.getStartDate();
                LocalDate endDate = memberAb.getEndDate();

                boolean isWithinValidPeriod = (endDate != null) && (
                        (startDate.isBefore(currentDate) || startDate.isEqual(currentDate)) &&
                                (endDate.isAfter(currentDate) ) );

                boolean isUnlimitedMembership = (endDate == null) &&
                        (!currentDate.isBefore(startDate)) &&
                        memberAb.getNbrSessionCarnet() >= 0;

                if (!memberAb.getAbStatus().equals("Annulé")) {
                    if (isMembershipExpired(memberAb)) {
                        memberAb.setAbStatus("Expiré");
                    } else if (memberAb.getAbStatus().equals("Programmé") && (isWithinValidPeriod || isUnlimitedMembership)) {
                        memberAb.setAbStatus("Active");
                    }
                }
            }
        }
        memberAbonnementRepo.saveAll(memberAbonnementList);
    }
}
