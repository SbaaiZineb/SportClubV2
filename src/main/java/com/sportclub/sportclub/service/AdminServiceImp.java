package com.sportclub.sportclub.service;

import com.sportclub.sportclub.entities.Role;
import com.sportclub.sportclub.entities.UserApp;
import com.sportclub.sportclub.repository.AdminRepo;
import com.sportclub.sportclub.repository.RoleRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Service
@Transactional
public class AdminServiceImp implements AdminService {
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    AdminRepo adminRepo;
    @Autowired
    RoleRepo roleRepo;

    @Override
    public void addAdmin(UserApp user) {
        adminRepo.save(user);
    }

    /*@Override
    public UserApp addNewUser(String pic, String name, String lname, String adress, String cin, LocalDate dob, int tele, Role roles, String email, String password,String confirmPass) {
        UserApp userApp = adminRepo.findByEmail(email);
        if (userApp != null) throw new RuntimeException("this user already exist");
        if (!password.equals(confirmPass)) throw new RuntimeException("Pas les memes");

        userApp = UserApp.builder()
                .name(name)
                .lname(lname)
                .adress(adress)
                .cin(cin)
                .dob(dob)
                .tele(tele)
                .roles(roles)
                .email(email)
                .password(passwordEncoder.encode(password))
                .build();

        return adminRepo.save(userApp);
    }

    @Override
    public Role addNewRole(String role) {
        Role appRole = roleRepo.findByRoleName(role);
        if (appRole != null) throw new RuntimeException("Role Already exist");
        appRole = Role.builder()

                .roleName(role)
                .build();
        return roleRepo.save(appRole);
    }

    @Override
    public void addRoleToUser(String email, String role) {
        UserApp userApp = adminRepo.findByEmail(email);
        Role appRole = roleRepo.findByRoleName(role);
        userApp.setRoles(appRole);

    }
*/
    @Override
    public long count() {
        return adminRepo.count();
    }

    @Override
    public List<UserApp> getAdminBynName(String name) {
        return adminRepo.findByNameContains(name);
    }

    @Override
    public void deleteAdmin(Long id) {
        adminRepo.deleteById(id);
    }

    @Override
    public Page<UserApp> findByAdminName(String mc, Pageable pageable) {
        return adminRepo.findByNameContains(mc, pageable);
    }

    @Override
    public UserApp getAdminById(Long id) {
        return adminRepo.findById(id).get();
    }

    @Override
    public void updateAdmin(UserApp admin) {
        adminRepo.save(admin);
    }

    public Page<UserApp> getUsersByRoles(String role, Pageable pageable) {
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.DESC, "id"));

        return adminRepo.findByRolesRoleNameContains(role, pageable);
    }

    @Override
    public List<UserApp> getAllAdmins() {
        return adminRepo.findAll();
    }

    @Override
    public UserApp loadUserByUsername(String email) {
        return adminRepo.findByEmail(email);
    }
    @Override
    public Boolean getByEmail(String email) {
        return adminRepo.findExistByEmail(email);
    }
}
