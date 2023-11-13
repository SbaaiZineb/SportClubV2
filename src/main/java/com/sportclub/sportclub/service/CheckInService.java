package com.sportclub.sportclub.service;

import com.sportclub.sportclub.entities.CheckIn;
import com.sportclub.sportclub.entities.Member;
import com.sportclub.sportclub.entities.Seance;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface CheckInService {
    List<CheckIn> getByMemberCheck(Member member);

    List<CheckIn> getBySession(Long id);

    List<CheckIn> getCheckInOfCurrenteek(LocalDate selectedDate);

    List<CheckIn> getByMember(Member member, LocalDate selectedDAte);

    List<CheckIn> getCheckInOfWeek();

    void addCheck(CheckIn checkIn);

    List<CheckIn> getAllCheckIns();


    List<CheckIn> getAllbyTime(LocalTime time);

    List<CheckIn> getCheckInByDate(LocalDate date);
}

