package com.sportclub.sportclub.controller;

import com.sportclub.sportclub.entities.CalendarEvent;
import com.sportclub.sportclub.entities.Coach;
import com.sportclub.sportclub.entities.Seance;
import com.sportclub.sportclub.repository.EventRepo;
import com.sportclub.sportclub.service.CoachService;
import com.sportclub.sportclub.service.MemberService;
import com.sportclub.sportclub.service.SeanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
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


    @GetMapping("/calendar")
    public String getCalendar(Model model) {
        System.out.println(eventRepo.findAll());
        return "calendar";
    }

    @GetMapping("/seanceList")
    public String getSeances(Model model, @RequestParam(name = "page", defaultValue = "0") int page,
                             @RequestParam(name = "size", defaultValue = "5") int size,
                             @RequestParam(name = "keyword", defaultValue = "") String kw
    ) {
        List<Coach> coaches = coachService.getAllCoachs();
        model.addAttribute("coaches", coaches);
        model.addAttribute("coach", new Coach());
//        List<String> options = Arrays.asList("MONDAY", "TUESDAY", "WEDNESDAY","THURSDAY","FRIDAY","SATURDAY","SUNDAY");
//        model.addAttribute("options", options);
        Page<Seance> pageS = service.findBySeanceName(kw, PageRequest.of(page, size));
        model.addAttribute("listSeance", pageS.getContent());
        model.addAttribute("pages", new int[pageS.getTotalPages()]);
        model.addAttribute("currentPage", page);
        model.addAttribute("keyword", kw);
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
     public String search( Model model, String seance) {

         if(seance!=null) {
             List<Seance> list = service.getSeanceBynName(seance);
             model.addAttribute("listSeance", list);
         }else {
             List<Seance> list = service.getAllSeance();
             model.addAttribute("listSeance", list);}
         return "seanceList";
     }
    @GetMapping("/addSeance")
    public String getAddSeance(Model model) {

        Seance seance = new Seance();
        model.addAttribute("seance", seance);
        return "seanceList";
    }

    @PostMapping("/addSeance")
    public String addSeance(@Validated Seance seance, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) return "seanceList";

        service.addSeance(seance);
        CalendarEvent calendarEvent = new CalendarEvent();
        calendarEvent.setTitle(seance.getClassName());
        calendarEvent.setStart(seance.getStartDate());
        calendarEvent.setStartTime(seance.getStartTime());
        calendarEvent.setEndTime(seance.getEndTime());
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
    public String deleteCells(@RequestParam("seanceId") Long[] seanceId) {
        // Perform the delete operation using the selected cell IDs

        for (Long cellId : seanceId) {

            service.deletSeance(cellId);
        }

        // Redirect to a success page or return a response as needed
        return "redirect:/seanceList";
    }

    @GetMapping("/deleteSeance")
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
    public String editSeance(@Validated Seance seance, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) return "updateSeanceModal";
        //TODO: Update session -> update event or add new event
        service.updateSeance(seance);
        return "redirect:/seanceList";
    }
}
