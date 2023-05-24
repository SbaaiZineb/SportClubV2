package com.sportclub.sportclub.service;

import com.sportclub.sportclub.entities.Coach;
import com.sportclub.sportclub.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AdminService {
    void addAdmin(User user);
    long count();
    List<User> getAdminBynName(String name);
    void deleteAdmin(Long id);
    Page<User> findByAdminName(String mc, Pageable pageable);
    User getAdminById(Long id);
    void updateAdmin(User admin);
    List<User> getAllAdmins();
    public Page<User> getUsersByRoles(String role,Pageable pageable);
}
