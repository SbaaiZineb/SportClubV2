package com.sportclub.sportclub.service;

import com.sportclub.sportclub.entities.User;
import com.sportclub.sportclub.repository.AdminRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class AdminServiceImp implements AdminService{
    @Autowired
    AdminRepo adminRepo;

    @Override
    public void addAdmin(User user) {
        adminRepo.save(user);
    }

    @Override
    public long count() {
        return adminRepo.count();
    }

    @Override
    public List<User> getAdminBynName(String name) {
        return adminRepo.findByNameContains(name);
    }

    @Override
    public void deleteAdmin(Long id) {
adminRepo.deleteById(id);
    }

    @Override
    public Page<User> findByAdminName(String mc, Pageable pageable) {
        return adminRepo.findByNameContains(mc,pageable);
    }

    @Override
    public User getAdminById(Long id) {
        return adminRepo.findById(id).get();
    }

    @Override
    public void updateAdmin(User admin) {
adminRepo.save(admin);
    }
    public Page<User> getUsersByRoles(String role,Pageable pageable) {

        return adminRepo.findByRolesRoleNameContains(role,pageable);
    }
    @Override
    public List<User> getAllAdmins() {
        return adminRepo.findAll();
    }
}
