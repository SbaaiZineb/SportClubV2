package com.sportclub.sportclub.controller;

import com.sportclub.sportclub.entities.CalendarEvent;
import com.sportclub.sportclub.repository.EventRepo;
import com.sportclub.sportclub.service.SeanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CalendarRestController {
    @Autowired
    SeanceService service;
    @Autowired
    EventRepo eventRepo;
    @GetMapping("/getSessions")
    public ResponseEntity<List<CalendarEvent> > getSession(Authentication authentication){
        String username=authentication.getName();
        //Get the sessions of the authenticated coach
        if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("COACH"))){
            return ResponseEntity.ok(eventRepo.findByUsername(username));
        }else {
            // Otherwise get All sessions
            return ResponseEntity.ok(eventRepo.findAll());
        }

    }
}
