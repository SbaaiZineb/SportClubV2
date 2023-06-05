package com.sportclub.sportclub.service;

import com.sportclub.sportclub.entities.CheckIn;
import com.sportclub.sportclub.entities.CheckInCoach;
import com.sportclub.sportclub.entities.Coach;
import com.sportclub.sportclub.entities.Seance;
import com.sportclub.sportclub.repository.CoachCheckInRepo;
import com.sportclub.sportclub.repository.SeanceRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
public class CoachCheckInService {
    @Autowired
    CoachCheckInRepo coachCheckRepo;
    @Autowired
    SeanceRepo seanceRepo;
    public List<CheckInCoach> getBySession(Long id) {
        Seance seance=seanceRepo.findById(id).get();
        return coachCheckRepo.findBySession(seance);
    }

    public List<CheckInCoach> getCheckInOfCurrentWeek(Coach coach, LocalDate selectedDate){
        LocalDate startOfWeek = selectedDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = selectedDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        return coachCheckRepo.findByCoachAndCheckinDateBetween(coach, startOfWeek,endOfWeek);
    };
    public void addCheck(CheckInCoach checkIn) {
        coachCheckRepo.save(checkIn);
    }

    public List<CheckInCoach> getAllCheckIns() {
        return coachCheckRepo.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }
    public List<CheckIn> getAllbyTime(LocalTime time) {
        return null;
    }
    public List<CheckInCoach> findLatest(LocalDate date) {
        Sort sortByDateDesc = Sort.by(Sort.Direction.DESC, "checkinTime");
        return coachCheckRepo.getCheckInByCheckinDate(date,sortByDateDesc);
    }
}
