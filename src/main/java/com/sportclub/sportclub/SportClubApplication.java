package com.sportclub.sportclub;

import com.sportclub.sportclub.entities.CheckIn;
import com.sportclub.sportclub.entities.Coach;
import com.sportclub.sportclub.entities.Member;
import com.sportclub.sportclub.entities.Seance;
import com.sportclub.sportclub.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class SportClubApplication  implements CommandLineRunner {

@Autowired
SeanceService seanceService;


    public static void main(String[] args) {
        SpringApplication.run(SportClubApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        for (Seance seance:seanceService.getAllSeance()
             ) {
            List<Integer> dayInt=new ArrayList<>();
            for (String day: seance.getDays()
            ) {
                DayOfWeek dayOfWeek=DayOfWeek.valueOf(day);
                Integer di=dayOfWeek.getValue();
                dayInt.add(di);

                System.out.println(di);
            }

            
        }
   }
//   @Bean
  /* CommandLineRunner commandLineRunnerUserDetails(AdminService adminService){
        return args -> {

            adminService.addNewUser("admin1","12345","12345");
            adminService.addRoleToUser("admin1","ADMIN");
        };
   }*/
    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
