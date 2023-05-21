package com.sportclub.sportclub.controller;

import com.sportclub.sportclub.entities.CheckIn;
import com.sportclub.sportclub.entities.Member;
import com.sportclub.sportclub.entities.Seance;
import com.sportclub.sportclub.repository.CheckInRepo;
import com.sportclub.sportclub.service.CheckInService;
import com.sportclub.sportclub.service.MemberService;
import com.sportclub.sportclub.service.SeanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
    @Autowired
    SeanceService seanceService;
    @Autowired
    CheckInService checkInService;
    @Autowired
    MemberService memberService;
    @Autowired
    CheckInRepo checkInRepo;

    @GetMapping("/enregistrement")
    public String getCheckinPage(Model model) {
        LocalDate localDate = LocalDate.now();

        List<Seance> seances = seanceService.getAllSeance();
        for (Seance sess:seances
             ) {
            model.addAttribute("count",c);
        }




        DayOfWeek dayOfWeek = localDate.getDayOfWeek();
        List<Seance> seanceList = new ArrayList<>();

        for (Seance session : seances) {
            List<String> days=session.getDays();
            for (String day:days
                 ) {
                DayOfWeek df=DayOfWeek.valueOf(day);
                if (df == dayOfWeek) {
                    seanceList.add(session);
                    LocalTime currentTime = LocalTime.now();
                    model.addAttribute("currentTime", currentTime);
                    model.addAttribute("TodaySession", seanceList);
                }
            }

        }
        List<CheckIn> checkIn=checkInService.findLatest(localDate);
        model.addAttribute("checkin",checkIn);
        return "checkIn";
    }

    @GetMapping("/getCheckedInMembers")
    public String getCheck(@RequestParam(name = "id") Long id, Model model) {
        List<CheckIn> checkIns = checkInService.getBySession(id);
        List<Member> members = new ArrayList<>();
        for (CheckIn check : checkIns
        ) {
            if (check.getCheckinDate().equals(LocalDate.now())) {
                Member member = check.getMember();
                members.add(member);

                model.addAttribute("members", members);
            }}
        return "checkedInModal";
    }

    @GetMapping("/checkin")
    public String checkMembers(Model model, @RequestParam(name = "id") Long id) {
        List<Member> members = memberService.getAllMembers();
        List<Member> listFilter=new ArrayList<>();
        for (Member member:members
        ) {
            LocalDate startDate = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            LocalDate endDate = startDate.plusDays(6);
            List<CheckIn> checkIns = checkInRepo.findByMemberAndAndCheckinDateBetween(member, startDate, endDate);
            int checkInCount=checkIns.size();
            int nbr= member.getAbonnement().getNbrSeance();
            if (checkInCount<nbr){
                listFilter.add(member);
            }
        }
        model.addAttribute("sessionId", id);
        System.out.println("id " + id);
        model.addAttribute("membersList", listFilter);
        return "modalCheckIn";
    }

    @PostMapping("/checkin")
    public String checkin(@Validated Long id, @RequestParam("selectedCells") Long[] selectedCells ,Model model) {

        for (Long cellId : selectedCells) {
            CheckIn checkIn=new CheckIn();
            Member member = memberService.getMemberById(cellId);
                checkIn.setMember(member);
                checkIn.setCheckinDate(LocalDate.now());
                checkIn.setCheckinTime(LocalTime.now());
                checkIn.setSession(seanceService.getSeanceById(id));
                checkInService.addCheck(checkIn);

        }
        return "redirect:/enregistrement";
    }
}
