package com.sportclub.sportclub.controller;

import com.sportclub.sportclub.entities.CalendarEvent;
import com.sportclub.sportclub.entities.Seance;
import com.sportclub.sportclub.repository.EventRepo;
import com.sportclub.sportclub.service.SeanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class CalendarController {
    @Autowired
    SeanceService service;
    @Autowired
    EventRepo eventRepo;
    @GetMapping("/getSessions")
    public ResponseEntity<List<CalendarEvent> > getSession(){
        return ResponseEntity.ok(eventRepo.findAll());
    }
}
