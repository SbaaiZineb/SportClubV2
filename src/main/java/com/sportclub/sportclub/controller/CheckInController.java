package com.sportclub.sportclub.controller;

import com.sportclub.sportclub.entities.*;
import com.sportclub.sportclub.repository.CheckInRepo;
import com.sportclub.sportclub.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class CheckInController {
    SeanceService seanceService;
    CheckInService checkInService;

    MemberService memberService;

    CheckInRepo checkInRepo;

    public CheckInController(SeanceService seanceService, CheckInService checkInService, MemberService memberService, CheckInRepo checkInRepo, CoachCheckInService coachCheckInService,CoachService coachService) {
        this.seanceService = seanceService;
        this.checkInService = checkInService;
        this.memberService = memberService;
        this.checkInRepo = checkInRepo;
        this.coachCheckInService = coachCheckInService;
        this.coachService=coachService;
    }
CoachService coachService;
    CoachCheckInService coachCheckInService;

    LocalDate localDate = LocalDate.now();
    DayOfWeek dayOfWeek = localDate.getDayOfWeek();
    @GetMapping("/enregistrement")
    public String getCheckinPage(Model model) {

        List<Seance> seances= seanceService.getAllSeance();
        List<Seance> seanceList = new ArrayList<>();

        for (Seance session : seances) {
            List<String> days = session.getDays();
            for (String day : days
            ) {
                DayOfWeek df = DayOfWeek.valueOf(day);
                System.out.println("Day is " + df + " " + day);
                if (df == dayOfWeek) {

                    seanceList.add(session);
                    LocalTime currentTime = LocalTime.now();
                    model.addAttribute("currentTime", currentTime);
                    model.addAttribute("TodaySession", seanceList);
                }
            }

        }
        List<CheckIn> checkIn = checkInService.findLatest(localDate);
        model.addAttribute("checkin", checkIn);
        return "checkIn";
    }
   /* @RequestMapping(path = { "membersToCheck/search"})
    public String search(Model model, String keyword) {

        Member MemberForm = new Member();
        model.addAttribute("MemberForm", MemberForm);
        List<Member> list;
        if (keyword != null) {
            list = memberService.getMemberBynName(keyword);
        } else {
            list = memberService.getAllMembers();
        }
        model.addAttribute("membersList", list);
        return "modalCheckIn";
    }*/

    @GetMapping("/getCheckedInMembers")
    public String getCheckMembers(@RequestParam(name = "id") Long id, Model model) {
        List<CheckIn> checkIns = checkInService.getBySession(id);
        List<Member> members = new ArrayList<>();
        for (CheckIn check : checkIns
        ) {
            if (check.getCheckinDate().equals(LocalDate.now())) {
                Member member = check.getMember();
                members.add(member);

                model.addAttribute("members", members);
            }
        }
        return "checkedInModal";
    }

    @GetMapping("/checkin")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SUBADMIN')")
    public String getCheckMembersIn(Model model, @RequestParam(name = "id") Long id) {
        List<Member> members = memberService.getAllMembers();
        List<Member> listFilter = new ArrayList<>();
        Seance seance=seanceService.getSeanceById(id);
        for (Member member : members
        ) {
            LocalDate startDate = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            LocalDate endDate = startDate.plusDays(6);
            List<CheckIn> checkIns = checkInRepo.findByMemberAndSessionAndCheckinDateBetween(member,seance, startDate, endDate);
            int checkInCount = checkIns.size();
            int nbr = member.getAbonnement().getNbrSeance();
            boolean hasCheckedInToday = checkIns.stream()
                    .anyMatch(checkIn -> checkIn.getCheckinDate().isEqual(LocalDate.now()));


            if (checkInCount < nbr && !hasCheckedInToday) {
                listFilter.add(member);
            }
        }
        model.addAttribute("sessionId", id);
        System.out.println("id " + id);
        model.addAttribute("membersList", listFilter);
        return "modalCheckIn";
    }

    @PostMapping("/checkin")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SUBADMIN')")
    public String checkin(@Validated Long id, @RequestParam("selectedCells") Long[] selectedCells, Model model) {

        for (Long cellId : selectedCells) {
            CheckIn checkIn = new CheckIn();
            Member member = memberService.getMemberById(cellId);
            checkIn.setMember(member);
            checkIn.setCheckinDate(LocalDate.now());
            checkIn.setCheckinTime(LocalTime.now());
            checkIn.setSession(seanceService.getSeanceById(id));
            checkInService.addCheck(checkIn);

        }
        return "redirect:/enregistrement";
    }

    // Manage coach check in
    @GetMapping("coach/enregistrement")
    public String getCheckPage(Model model, Authentication authentication) {

        String username = authentication.getName();
        List<Seance> seances;
        if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("COACH"))) {
            seances = seanceService.getBYCoachEmail(username);
        } else {
            seances = seanceService.getAllSeance();
        }
        List<Seance> seanceList = new ArrayList<>();

        for (Seance session : seances) {
            List<String> days = session.getDays();
            for (String day : days
            ) {
                DayOfWeek df = DayOfWeek.valueOf(day);
                System.out.println("Day is " + df + " " + day);
                if (df == dayOfWeek) {

                    seanceList.add(session);
                    LocalTime currentTime = LocalTime.now();
                    model.addAttribute("currentTime", currentTime);
                    model.addAttribute("TodaySessions", seanceList);
                }
            }

        }
        List<CheckInCoach> checkIn = coachCheckInService.findLatest(localDate);
        model.addAttribute("checkin", checkIn);
        return "coachCheckIn";
    }

    @GetMapping("/coachcheckin")
    @PreAuthorize("hasAuthority('COACH')")
    public String coachCheckin( @RequestParam(name = "id") Long id,Authentication authentication) {

        System.out.println("session id is ----> "+id);
            CheckInCoach checkIn = new CheckInCoach();
            String email=authentication.getName();
            Coach coach=coachService.getCoachByEmail(email);
            checkIn.setCoach(coach);
            checkIn.setCheckinDate(LocalDate.now());
            checkIn.setCheckinTime(LocalTime.now());
            checkIn.setSession(seanceService.getSeanceById(id));
            coachCheckInService.addCheck(checkIn);


        return "redirect:/coach/enregistrement";
    }
}
