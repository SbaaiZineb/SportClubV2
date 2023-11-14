package com.sportclub.sportclub;

import com.sportclub.sportclub.entities.Gym;
import com.sportclub.sportclub.entities.Role;
import com.sportclub.sportclub.entities.UserApp;
import com.sportclub.sportclub.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@EnableScheduling
public class SportClubApplication implements CommandLineRunner {

    GymService gymService;
    SeanceService seanceService;
    AdminService adminService;
    RoleService roleService;

    @Autowired
    public SportClubApplication(SeanceService seanceService, AdminService adminService, GymService gymService, RoleService roleService) {
        this.seanceService = seanceService;
        this.adminService = adminService;
        this.gymService = gymService;
        this.roleService = roleService;
    }


    public static void main(String[] args) {
        SpringApplication.run(SportClubApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

//Add Roles if don't exist

        Role admin = roleService.getRoleByName("ADMIN");
        Role coach = roleService.getRoleByName("COACH");
        Role user = roleService.getRoleByName("ADHERENT");

        if (admin == null) {
            roleService.addRole(new Role(1L, "ADMIN"));
        }

        if (coach == null) {
            roleService.addRole(new Role(2L, "COACH"));
        }

        if (user == null) {
            roleService.addRole(new Role(3L, "ADHERENT"));
        }


        // Check if the user with the username "admin" already exists
        UserApp existingUser = adminService.loadUserByUsername("admin");


        if (existingUser == null) {
            // User doesn't exist, so add it
            UserApp newUser = new UserApp("admin", passwordEncoder().encode("admin"),roleService.getRoleByName("ADMIN"));
            adminService.addAdmin(newUser);

        } else {
            System.out.println("Already exists !!");
        }

        Gym gym = gymService.getById(1L);
        if (gym == null) {

            //ADD default information for the gym

            Gym newGym = new Gym(1L, "sport club", "address", "Email", " ", "Terms and conditions", "About Us", 00000, "WebSite", "Code Postal", "Morocco");
            gymService.addGymInfo(newGym);
        } else {
            System.out.println("No need to Add !!!");
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
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
