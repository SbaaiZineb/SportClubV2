package com.sportclub.sportclub.controller;

import com.sportclub.sportclub.entities.*;
import com.sportclub.sportclub.repository.EventRepo;
import com.sportclub.sportclub.repository.SeanceRepo;
import com.sportclub.sportclub.service.CoachService;
import com.sportclub.sportclub.service.MemberService;
import com.sportclub.sportclub.service.NotificationService;
import com.sportclub.sportclub.service.SeanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Controller
public class SeanceController {

    @Autowired
    SeanceService service;
    @Autowired
    CoachService coachService;

    @Autowired
    MemberService memberService;
    @Autowired
    EventRepo eventRepo;
    @Autowired
    SeanceRepo seanceRepo;
    @Autowired
    NotificationService notificationService;


    @GetMapping("/calendar")
    public String getCalendar(Model model) {
        System.out.println(eventRepo.findAll());
        return "calendar";
    }

    @GetMapping("/seanceList")
    public String getSeances(Model model, @RequestParam(name = "page", defaultValue = "0") int page,
                             @RequestParam(name = "size", defaultValue = "5") int size,
                             @RequestParam(name = "keyword", defaultValue = "") String kw, Authentication authentication
    ) {
        String username = authentication.getName();

        if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("COACH"))) {
            Page<Seance> seancePage = service.findSeanceByCoachEmail(username, PageRequest.of(page, size));
            model.addAttribute("listSeance", seancePage.getContent());
            model.addAttribute("pages", new int[seancePage.getTotalPages()]);
            model.addAttribute("currentPage", page);
            model.addAttribute("keyword", kw);
        } else {
            Page<Seance> pageS = service.findBySeanceName(kw, PageRequest.of(page, size));

            model.addAttribute("listSeance", pageS.getContent());
            model.addAttribute("pages", new int[pageS.getTotalPages()]);
            model.addAttribute("currentPage", page);
            model.addAttribute("keyword", kw);
        }

        System.out.println("username !!!!" + username);
//Add coach to the dropdown list
        List<Coach> coaches = coachService.getAllCoachs();
        model.addAttribute("coaches", coaches);
        model.addAttribute("coach", new Coach());


        List<Seance> se = service.getAllSeance();
        for (Seance seance : se
        ) {
            Duration duration = Duration.between(seance.getStartTime(), seance.getEndTime());
            long hours = duration.toHours();
            long minutes = duration.toMinutesPart();

            String formattedDuration = String.format("%02d:%02d", hours, minutes);
            model.addAttribute("duration", formattedDuration);
        }
        Seance seance = new Seance();
        model.addAttribute("seance", seance);
        return "seanceList";

    }

    @RequestMapping(path = {"/seanceList/search"})
    public String search(Model model, String seance) {

        if (seance != null) {
            List<Seance> list = service.getSeanceBynName(seance);
            model.addAttribute("listSeance", list);
        } else {
            List<Seance> list = service.getAllSeance();
            model.addAttribute("listSeance", list);
        }
        return "seanceList";
    }

    @GetMapping("/addSeance")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SUBADMIN')")
    public String getAddSeance(Model model) {

        Seance seance = new Seance();
        model.addAttribute("seance", seance);
        return "seanceList";
    }

    public Date convertToDateViaSqlDate(LocalDate dateToConvert) {
        return java.sql.Date.valueOf(dateToConvert);
    }

    @PostMapping("/addSeance")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SUBADMIN')")
    public String addSeance(@Validated Seance seance, BindingResult bindingResult, Authentication authentication) {
        if (bindingResult.hasErrors()) return "seanceList";


        String username = authentication.getName();
        service.addSeance(seance);
        //Add Session Notification
        Notification notification = new Notification();
        notification.setTimestamp(LocalDateTime.now());
        notification.setMessage(username + " vous a affecté à une nouvelle session consultez votre agenda");
        List<UserApp> userApps = new ArrayList<>();
        userApps.add(seance.getCoach());
        notification.setRecipient(userApps);
        notification.setTitle("Nouvelle seance");
        notificationService.addNotification(notification);

        //Add new Event based on the added session
        CalendarEvent calendarEvent = new CalendarEvent();
        calendarEvent.setTitle(seance.getClassName());
        calendarEvent.setStart(seance.getStartDate());
        calendarEvent.setStartTime(seance.getStartTime());
        calendarEvent.setEndTime(seance.getEndTime());
        calendarEvent.setUsername(seance.getCoach().getEmail());
        int nbrToAdd = seance.getNumWeeks();
        LocalDate newEnd = seance.getStartDate().plusWeeks(nbrToAdd);
        calendarEvent.setEndRecur(newEnd);
        calendarEvent.setStartRecur(seance.getStartDate());
        List<Integer> dayInt = new ArrayList<>();
        for (String day : seance.getDays()
        ) {
            if (day != null) {
                try {
                    System.out.println(day);
                    DayOfWeek dayOfWeek = DayOfWeek.valueOf(day);
                    Integer di = dayOfWeek.getValue();
                    dayInt.add(di);
                    System.out.println(dayOfWeek);
                    System.out.println(di);
                    System.out.println(dayInt);
                    calendarEvent.setDaysOfWeek(dayInt);
                } catch (IllegalArgumentException e) {
                    System.out.println("Error " + e);
                }
            }

        }
        eventRepo.save(calendarEvent);

        return "redirect:/seanceList";
    }

    @PostMapping("/deleteSessions")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SUBADMIN')")
    public String deleteCells(@RequestParam("seanceId") Long[] seanceId) {
        // Perform the delete operation using the selected cell IDs
        Seance seance;
        for (Long cellId : seanceId) {
            seance = service.getSeanceById(cellId);
            service.deletSeance(cellId);
            List<CalendarEvent> calendarEvents = eventRepo.findAll();
            for (CalendarEvent event : calendarEvents
            ) {
                if (event.getTitle().equals(seance.getClassName()) && event.getStart().equals(seance.getStartDate()) && event.getStartTime().equals(seance.getStartTime()) && event.getEndTime().equals(seance.getEndTime())) {
                    eventRepo.delete(event);
                }
            }

        }

        // Redirect to a success page or return a response as needed
        return "redirect:/seanceList";
    }

    @GetMapping("/deleteSeance")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SUBADMIN')")
    public String deleteSeance(@RequestParam(name = "id") Long id, String keyword, int page) {
        Seance seance = service.getSeanceById(id);
        service.deletSeance(id);
        List<CalendarEvent> calendarEvents = eventRepo.findAll();
        for (CalendarEvent event : calendarEvents
        ) {
            if (event.getTitle().equals(seance.getClassName()) && event.getStart().equals(seance.getStartDate()) && event.getStartTime().equals(seance.getStartTime()) && event.getEndTime().equals(seance.getEndTime())) {
                eventRepo.delete(event);
            }
        }

        return "redirect:/seanceList?page=" + page + "&keyword=" + keyword;
    }

    @GetMapping("/editSeance")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SUBADMIN')")
    public String editSeance(@RequestParam(name = "id") Long id, Model model) {
        List<Coach> coaches = coachService.getAllCoachs();
        model.addAttribute("coaches", coaches);
        model.addAttribute("coach", new Coach());
        Seance seance = service.getSeanceById(id);
        seance.setStartDate(LocalDate.parse(seance.getStartDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));

        model.addAttribute("seance", seance);
        return "updateSeanceModal";
    }

    @PostMapping("/editSeance")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SUBADMIN')")
    public String editSeance(@Validated Seance seance, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) return "updateSeanceModal";

        //TODO: Update session -> update event or add new event
        service.updateSeance(seance);
        return "redirect:/seanceList";
    }
}
