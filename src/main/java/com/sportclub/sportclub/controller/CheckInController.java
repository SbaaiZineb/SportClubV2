package com.sportclub.sportclub.controller;

import com.sportclub.sportclub.entities.Seance;
import com.sportclub.sportclub.service.SeanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Controller
public class CheckInController {
    @Autowired
    SeanceService seanceService;
    @GetMapping("/checkin")
    public String getCheckinPage(Model model){
        LocalDate localDate=LocalDate.now();
        List<Seance> seances=seanceService.getSeanceByStartDate(localDate);
        LocalTime currentTime = LocalTime.now();
        model.addAttribute("currentTime", currentTime);
        model.addAttribute("TodaySession",seances);
        return "checkIn";
    }

}
