package com.sportclub.sportclub;

import com.sportclub.sportclub.entities.Role;
import com.sportclub.sportclub.entities.UserApp;
import com.sportclub.sportclub.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class SportClubApplication  implements CommandLineRunner {

    SeanceService seanceService;
    AdminService adminService;
    @Autowired
    public SportClubApplication(SeanceService seanceService, AdminService adminService) {
        this.seanceService = seanceService;
        this.adminService = adminService;
    }




    public static void main(String[] args) {
        SpringApplication.run(SportClubApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        // Check if the user with the username "admin3" already exists
        UserApp existingUser = adminService.loadUserByUsername("admin");

        if (existingUser == null) {
            // User doesn't exist, so add it
            UserApp newUser = new UserApp("admin", passwordEncoder().encode("admin"), new Role(1L,"ADMIN"));
            adminService.addAdmin(newUser);
        } else {
            System.out.println("Already exists !!");
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
