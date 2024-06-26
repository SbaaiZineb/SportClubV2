package com.sportclub.sportclub.controller;

import com.sportclub.sportclub.entities.*;
import com.sportclub.sportclub.repository.EventRepo;
import com.sportclub.sportclub.repository.SeanceRepo;
import com.sportclub.sportclub.service.CoachService;
import com.sportclub.sportclub.service.MemberService;
import com.sportclub.sportclub.service.NotificationService;
import com.sportclub.sportclub.service.SeanceService;
import jakarta.servlet.http.HttpServletRequest;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('EMPLOYEE') or hasAuthority('COACH')")

    public String getCalendar(Model model) {
        System.out.println(eventRepo.findAll());
        return "calendar";
    }

    @GetMapping("/seanceList")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('EMPLOYEE') or hasAuthority('COACH')")

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

    @GetMapping("/seanceList/search")
    public String search(@RequestParam("keyword") String keyword, Model model) {
        model.addAttribute("seance", new Seance());
        List<Seance> searchResults;

        if (!keyword.isEmpty()) {
            searchResults = service.getSeanceBynName(keyword);

        } else {

            return "redirect:/seanceList";
        }

        model.addAttribute("listSeance", searchResults);
        model.addAttribute("keyword", keyword);

        return "seanceList";
    }

    @GetMapping("/addSeance")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('EMPLOYEE')")
    public String getAddSeance(Model model) {
        List<Coach> coaches = coachService.getAllCoachs();
        model.addAttribute("coaches", coaches);
        model.addAttribute("coach", new Coach());
        Seance seance = new Seance();
        model.addAttribute("seance", seance);
        return "seanceList";
    }


    @PostMapping("/addSeance")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('EMPLOYEE')")
    public String addSeance(@Validated Seance seance, BindingResult bindingResult, Authentication authentication,RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) return "seanceList";
        CalendarEvent calendarEvent = new CalendarEvent();


        String username = authentication.getName();
        if (seance != null) {
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
            try {
                //Add new Event based on the added session

                calendarEvent.setId(seance.getId());
                calendarEvent.setTitle(seance.getClassName());
                calendarEvent.setStart(seance.getStartDate());
                calendarEvent.setStartTime(seance.getStartTime());
                calendarEvent.setEndTime(seance.getEndTime());
                if (seance.getCoach() != null) {
                    calendarEvent.setUsername(seance.getCoach().getEmail());

                } else {
                    calendarEvent.setUsername(null);

                }
                int nbrToAdd = seance.getNumWeeks();

                if (nbrToAdd == 0) {
                    calendarEvent.setEndRecur(null);
                    calendarEvent.setStartRecur(null);
                } else {
                    LocalDate newEnd = seance.getStartDate().plusWeeks(nbrToAdd);
                    calendarEvent.setEndRecur(newEnd);
                    calendarEvent.setStartRecur(seance.getStartDate());
                }

                List<Integer> dayInt = new ArrayList<>();
                if (seance.getDays() != null) {
                    for (String day : seance.getDays()
                    ) {
                        if (day != null) {
                            try {
                                System.out.println(day);
                                DayOfWeek dayOfWeek = DayOfWeek.valueOf(day);
                                Integer di = dayOfWeek.getValue();
                                dayInt.add(di);

                            } catch (IllegalArgumentException e) {
                                System.out.println("Error " + e);
                            }
                        } else if (dayInt.isEmpty()) {
                            // Add all days of the week to the dayInt list
                            for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
                                dayInt.add(dayOfWeek.getValue());
                            }
                        }
                        calendarEvent.setDaysOfWeek(dayInt);

                    }
                }
                eventRepo.save(calendarEvent);
            } catch (Exception e) {
                System.out.println("Something Wrong!!!! " + e);
            }
        }
        redirectAttributes.addFlashAttribute("successMessage", "Seance ajouter avec succès!");

        return "redirect:/seanceList";
    }

    @PostMapping("/deleteSessions")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('EMPLOYEE')")
    public String deleteCells(@RequestParam(value = "seanceId", required = false) Long[] seanceId, RedirectAttributes redirectAttributes) {
        // Perform the delete operation using the selected cell IDs
        // Seance seance;
        if (seanceId != null && seanceId.length > 0) {

            for (Long cellId : seanceId) {

                Seance seance = service.getSeanceById(cellId);
                System.out.println(seance);
                service.deletSeance(cellId);
                CalendarEvent event = eventRepo.findById(cellId).get();
                eventRepo.delete(event);

            }
            redirectAttributes.addFlashAttribute("successMessage", "Seance supprimés avec succès!");
        } else {
            // No checkboxes were selected, handle accordingly (e.g., show an error message)
            redirectAttributes.addFlashAttribute("errorMessage", "Aucune cellule sélectionnée pour suppression.");
        }
        // Redirect to a success page or return a response as needed
        return "redirect:/seanceList";
    }

    // search by coach
    @GetMapping("/sessions/search")
    public String searchSessionsByCoach(@RequestParam(required = false, name = "coachId") Long coachId, Model model) {
        Seance seance = new Seance();
        List<Seance> sessions;
        if (coachId != null) {
            model.addAttribute("seance", seance);
            model.addAttribute("coachId", coachId);
            sessions = service.getSeanceByCoach(coachId);
            model.addAttribute("listSeance", sessions);
            List<Coach> coaches = coachService.getAllCoachs();
            model.addAttribute("coaches", coaches);
        } else {
            return "redirect:/seanceList";
        }

        return "seanceList";
    }


    @GetMapping("/deleteSeance")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('EMPLOYEE')")
    public String deleteSeance(@RequestParam(name = "id") Long id) {
        Seance seance = service.getSeanceById(id);
        service.deletSeance(id);
        CalendarEvent event = eventRepo.findById(id).get();

        eventRepo.delete(event);
        return "redirect:/seanceList";
    }

    @GetMapping("/editSeance")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('EMPLOYEE')")
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
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('EMPLOYEE')")
    public String editSeance(@Validated Seance seance, BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) return "error";
        String[] selectedDays = request.getParameterValues("days");
        if (selectedDays == null) {
            seance.setDays(null);
        } else {
            seance.setDays(Arrays.asList(selectedDays));
        }

        service.updateSeance(seance);
        CalendarEvent calendarEvent = eventRepo.findById(seance.getId()).get();
        //Update the Event based on the updated  session
        calendarEvent.setTitle(seance.getClassName());
        calendarEvent.setStart(seance.getStartDate());
        calendarEvent.setStartTime(seance.getStartTime());
        calendarEvent.setEndTime(seance.getEndTime());
        calendarEvent.setUsername(seance.getCoach().getEmail());
        int nbrToAdd = seance.getNumWeeks();

        if (nbrToAdd == 0) {
            calendarEvent.setEndRecur(null);
            calendarEvent.setStartRecur(null);
        } else {
            LocalDate newEnd = seance.getStartDate().plusWeeks(nbrToAdd);
            calendarEvent.setEndRecur(newEnd);
            calendarEvent.setStartRecur(seance.getStartDate());
        }
        List<Integer> dayInt = new ArrayList<>();
        if (seance.getDays() != null) {
            for (String day : seance.getDays()
            ) {
                if (day != null) {
                    try {

                        DayOfWeek dayOfWeek = DayOfWeek.valueOf(day);
                        Integer di = dayOfWeek.getValue();
                        dayInt.add(di);

                        calendarEvent.setDaysOfWeek(dayInt);
                    } catch (IllegalArgumentException e) {
                        System.out.println("Error " + e);
                    }
                } else if (dayInt.isEmpty()) {
                    // Add all days of the week to the dayInt list
                    for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
                        dayInt.add(dayOfWeek.getValue());
                    }
                }
                calendarEvent.setDaysOfWeek(dayInt);

            }
        }

        eventRepo.save(calendarEvent);


        return "redirect:/seanceList";
    }
}
