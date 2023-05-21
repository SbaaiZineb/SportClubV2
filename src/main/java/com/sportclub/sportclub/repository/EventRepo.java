package com.sportclub.sportclub.repository;

import com.sportclub.sportclub.entities.CalendarEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepo extends JpaRepository<CalendarEvent,Long > {

}
