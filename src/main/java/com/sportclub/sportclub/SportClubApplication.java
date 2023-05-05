package com.sportclub.sportclub;

import com.sportclub.sportclub.entities.Coach;
import com.sportclub.sportclub.entities.Seance;
import com.sportclub.sportclub.service.CoachService;
import com.sportclub.sportclub.service.SeanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SportClubApplication  implements CommandLineRunner {
CoachService coachService;
@Autowired
SeanceService seanceService;
    public static void main(String[] args) {
        SpringApplication.run(SportClubApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        /*for (Seance seance:seanceService.getAllSeance()
             ) {
            System.out.println("id "+seance.getId()+"\nname " +seance.getClassName()+ "\ncoach "+seance.getCoach());
            
        }*/
    }
}
