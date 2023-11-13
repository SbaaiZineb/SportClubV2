package com.sportclub.sportclub.service;

import com.sportclub.sportclub.entities.CheckIn;
import com.sportclub.sportclub.entities.Member;
import com.sportclub.sportclub.entities.Seance;
import com.sportclub.sportclub.repository.CheckInRepo;
import com.sportclub.sportclub.repository.SeanceRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;

@Service
public class CheckInServiceImp implements CheckInService{
    @Autowired
    CheckInRepo checkInRepo;
    @Autowired
    SeanceRepo seanceRepo;

    @Override
    public List<CheckIn> getByMemberCheck(Member member) {
        return checkInRepo.findByMember(member);
    }

    @Override
    public List<CheckIn> getBySession(Long id) {
        Seance seance=seanceRepo.findById(id).get();
        return checkInRepo.findBySession(seance);
    }



    @Override
    public List<CheckIn> getCheckInOfWeek() {
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(java.time.DayOfWeek.MONDAY);
        LocalDate endOfWeek = today.with(java.time.DayOfWeek.SUNDAY);
        return checkInRepo.findByCheckinDateBetween(startOfWeek,endOfWeek);
    }

    @Override
    public List<CheckIn> getCheckInOfCurrenteek(LocalDate selectedDate) {

        LocalDate startOfWeek = selectedDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = selectedDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        return checkInRepo.findByCheckinDateBetween(startOfWeek,endOfWeek);
    }

    @Override
    public List<CheckIn> getByMember(Member member,LocalDate selectedDAte) {
        LocalDate startOfMonth = selectedDAte.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate endOfMonth = selectedDAte.with(TemporalAdjusters.lastDayOfMonth());
        return checkInRepo.findByMemberAndCheckinDateBetween(member,startOfMonth, endOfMonth);
    }

    @Override
    public void addCheck(CheckIn checkIn) {
        checkInRepo.save(checkIn);
    }

    @Override
    public List<CheckIn> getAllCheckIns() {
        return checkInRepo.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }


    @Override
    public List<CheckIn> getAllbyTime(LocalTime time) {
        return null;
    }

    @Override
    public List<CheckIn> getCheckInByDate(LocalDate date) {
        Sort sortByDateDesc = Sort.by(Sort.Direction.DESC, "checkinTime");
        return checkInRepo.getCheckInByCheckinDate(date,sortByDateDesc);
    }


}
