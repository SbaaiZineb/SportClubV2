package com.sportclub.sportclub.repository;

import com.sportclub.sportclub.entities.CalendarEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRepo extends JpaRepository<CalendarEvent,Long > {
List<CalendarEvent> findByUsername(String username);
}
