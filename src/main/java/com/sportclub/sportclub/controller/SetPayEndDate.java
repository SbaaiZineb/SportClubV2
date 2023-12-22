package com.sportclub.sportclub.controller;

import com.sportclub.sportclub.entities.Paiement;

import java.time.LocalDate;

public class SetPayEndDate {
    public void setPayEndDate(String period, Paiement paiement){
        switch (period) {
            case "12" -> {
                LocalDate end = paiement.getStart_date().plusYears(1);
                paiement.setEnd_date(end);
            }
            case "3" -> {
                LocalDate end = paiement.getStart_date().plusMonths(3);
                paiement.setEnd_date(end);
            }
            case "1" -> {
                LocalDate end = paiement.getStart_date().plusMonths(1);
                paiement.setEnd_date(end);
            }
            case "6" -> {
                LocalDate end = paiement.getStart_date().plusMonths(6);
                paiement.setEnd_date(end);
            }
            case "2" -> {
                LocalDate end = paiement.getStart_date().plusMonths(2);
                paiement.setEnd_date(end);
            }
            case "0" -> {
                paiement.setEnd_date(null);
            }
        }
    }
}
