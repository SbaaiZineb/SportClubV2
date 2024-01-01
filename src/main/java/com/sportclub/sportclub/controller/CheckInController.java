package com.sportclub.sportclub.controller;

import com.sportclub.sportclub.entities.*;
import com.sportclub.sportclub.repository.CheckInRepo;
import com.sportclub.sportclub.service.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

@Controller
public class CheckInController {
    SeanceService seanceService;
    CheckInService checkInService;

    MemberService memberService;
    AdminService adminService;
    CheckInRepo checkInRepo;

    public CheckInController(SeanceService seanceService, CheckInService checkInService, MemberService memberService, AdminService adminService, CheckInRepo checkInRepo, CoachCheckInService coachCheckInService, CoachService coachService) {
        this.seanceService = seanceService;
        this.checkInService = checkInService;
        this.memberService = memberService;
        this.checkInRepo = checkInRepo;
        this.coachCheckInService = coachCheckInService;
        this.coachService = coachService;
        this.adminService = adminService;
    }

    CoachService coachService;
    CoachCheckInService coachCheckInService;

    LocalDate localDate = LocalDate.now();
    DayOfWeek dayOfWeek = localDate.getDayOfWeek();

    @GetMapping("/enregistrement")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('EMPLOYEE')")
    public String getCheckinPage(Model model) {
        try {
            List<Seance> seances = seanceService.getAllSeance();
        /*List<Seance> seanceList = new ArrayList<>();

        for (Seance session : seances) {
            List<String> days = session.getDays();
            for (String day : days
            ) {
                DayOfWeek df = DayOfWeek.valueOf(day);
                System.out.println("Day is " + df + " " + day);
                int nbrToAdd = session.getNumWeeks();
                LocalDate newEnd = session.getStartDate().plusWeeks(nbrToAdd);
                System.out.println(newEnd);
                if ((df == dayOfWeek && LocalDate.now().isBefore(newEnd)) || nbrToAdd == 0) {

                    seanceList.add(session);
                    model.addAttribute("TodaySession", seanceList);
                    LocalTime currentTime = LocalTime.now();
                    model.addAttribute("currentTime", currentTime);

                }
                System.out.println(seanceList);
            }

        } */
            model.addAttribute("TodaySession", seances);
            LocalTime currentTime = LocalTime.now();
            model.addAttribute("currentTime", currentTime);

            model.addAttribute("today", LocalDate.now());
           // List<CheckIn> checkIns = checkInService.getCheckInByDate(localDate);

            List<CheckIn> filteredCheckIns = new ArrayList<>();
            List<CheckIn> checkIns = checkInService.getAllCheckIns();
            for (CheckIn checkIn:checkIns){
                if(checkIn.getMember()!=null){
                    filteredCheckIns.add(checkIn);
                }
            }
            model.addAttribute("checkin", filteredCheckIns);
            return "checkIn";
        } catch (Exception e) {
            return "error";
        }
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
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('EMPLOYEE')")
    public String getCheckMembersIn(Model model, @RequestParam(name = "id") Long id) {
        List<Member> members = memberService.getAllMembers();
        List<Member> listFilter = new ArrayList<>();
        Seance seance = seanceService.getSeanceById(id);

        for (Member member : members
        ) {
            LocalDate startDate = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            LocalDate endDate = startDate.plusDays(6);
            List<CheckIn> checkIns = checkInRepo.findByMemberAndSessionAndCheckinDateBetween(member, seance, startDate, endDate);
//            int checkInCount = checkIns.size();
//
//            int nbr = member.getAbonnement().getNbrSeance();
            boolean hasCheckedInToday = checkIns.stream()
                    .anyMatch(checkIn -> checkIn.getCheckinDate().isEqual(LocalDate.now()));


            if (!hasCheckedInToday) {
                listFilter.add(member);
            }
        }
        model.addAttribute("sessionId", id);
        System.out.println("id " + id);
        model.addAttribute("membersList", listFilter);
        return "modalCheckIn";
    }

    @PostMapping("/checkin")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('EMPLOYEE')")
    public String checkin(@Validated Long id, @RequestParam("selectedCells") Long[] selectedCells, Authentication authentication,
                          RedirectAttributes redirectAttributes) {

        UserApp user = adminService.loadUserByUsername(authentication.getName());
        String userRole = user.getRoles().getRoleName();


        for (Long cellId : selectedCells) {

            CheckIn checkIn = new CheckIn();
            Member member = memberService.getMemberById(cellId);
            checkIn.setMember(member);
            checkIn.setCheckinDate(LocalDate.now());
            checkIn.setCheckinTime(LocalTime.now());

            checkIn.setSession(seanceService.getSeanceById(id));

            checkInService.addCheck(checkIn);

            // Decrement the number of sessions in the current "Carnet" by one when check into a session
            int nbrSessionCurrentCarnet = member.getNbrSessionCurrentCarnet();
            if (nbrSessionCurrentCarnet != 0) {
                nbrSessionCurrentCarnet = nbrSessionCurrentCarnet - 1;
                member.setNbrSessionCurrentCarnet(nbrSessionCurrentCarnet);
                memberService.updateMember(member);

            }

            // Add success message to be displayed on the redirected page
            redirectAttributes.addFlashAttribute("successMessage", "L'enregistrement du membre s'est bien déroulé!");


        }
        if (userRole.equals("EMPLOYEE")) {
            return "redirect:/employee/enregistrement";
        }
        return "redirect:/enregistrement";
    }


    // Manage coach check in
    @GetMapping("coach/enregistre")
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
                int nbrToAdd = session.getNumWeeks();
                LocalDate newEnd = session.getStartDate().plusWeeks(nbrToAdd);
                System.out.println(newEnd);
                if (df == dayOfWeek && LocalDate.now().isBefore(newEnd)) {

                    seanceList.add(session);
                    LocalTime currentTime = LocalTime.now();
                    model.addAttribute("currentTime", currentTime);
                    model.addAttribute("TodaySessions", seanceList);
                }
            }

        }
        model.addAttribute("currentDate", LocalDate.now());
        List<CheckInCoach> checkIn = coachCheckInService.findLatest(localDate);
        model.addAttribute("checkin", checkIn);
        return "coachCheckIn";
    }

    @GetMapping("/coachcheckin")
    @PreAuthorize("hasAuthority('COACH')")
    public String coachCheckin(@RequestParam(name = "id") Long id, Authentication authentication) {

        System.out.println("session id is ----> " + id);
        CheckInCoach checkIn = new CheckInCoach();
        String email = authentication.getName();
        Coach coach = coachService.getCoachByEmail(email);
        checkIn.setCoach(coach);
        checkIn.setCheckinDate(LocalDate.now());
        checkIn.setCheckinTime(LocalTime.now());
        checkIn.setSession(seanceService.getSeanceById(id));
        coachCheckInService.addCheck(checkIn);


        return "redirect:/coach/enregistre";
    }
}
