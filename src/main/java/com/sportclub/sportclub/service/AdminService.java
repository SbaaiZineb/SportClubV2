package com.sportclub.sportclub.service;

import com.sportclub.sportclub.entities.Coach;
import com.sportclub.sportclub.entities.Role;
import com.sportclub.sportclub.entities.UserApp;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface AdminService {
  Boolean getByEmail(String email);
    void addAdmin(UserApp user);
  /*  UserApp addNewUser(String pic,String name, String lname, String adress, String cin, LocalDate dob, int tele, Role roles, String email, String password,String confirmPass);
    Role addNewRole(String role);
    void addRoleToUser(String email,String role);*/
    long count();
    List<UserApp> getAdminBynName(String name);
    void deleteAdmin(Long id);
    Page<UserApp> findByAdminName(String mc, Pageable pageable);
    UserApp getAdminById(Long id);
    void updateAdmin(UserApp admin);
    List<UserApp> getAllAdmins();
    UserApp loadUserByUsername(String email);
    public Page<UserApp> getUsersByRoles(Pageable pageable);
    List<UserApp> getByCin(String cin);
    List<UserApp> getByTele(String Tele);
}
