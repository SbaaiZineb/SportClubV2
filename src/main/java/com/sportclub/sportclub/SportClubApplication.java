package com.sportclub.sportclub;

import com.sportclub.sportclub.entities.Coach;
import com.sportclub.sportclub.entities.Member;
import com.sportclub.sportclub.entities.Seance;
import com.sportclub.sportclub.service.AbonnementService;
import com.sportclub.sportclub.service.CoachService;
import com.sportclub.sportclub.service.MemberService;
import com.sportclub.sportclub.service.SeanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
/*
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
*/

@SpringBootApplication
public class SportClubApplication  implements CommandLineRunner {
CoachService coachService;
//@Autowired
//SeanceService seanceService;
@Autowired
    AbonnementService abonnementService;
    @Autowired
    MemberService memberService;
    public static void main(String[] args) {
        SpringApplication.run(SportClubApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        /*for (Seance seance:seanceService.getAllSeance()
             ) {
            System.out.println("id "+seance.getId()+"\nname " +seance.getClassName()+ "\ncoach "+seance.getCoach());
            
        }*/
        for (Member member:memberService.getMemberByMembership(2L)
             ) {
            System.out.println("id "+member.getId()+"\nname "+member.getName());
        }
    }
//    @Bean
//    PasswordEncoder passwordEncoder(){
//        return new BCryptPasswordEncoder();
//    }
}
