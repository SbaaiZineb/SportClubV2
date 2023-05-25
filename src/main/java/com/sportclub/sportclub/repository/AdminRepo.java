package com.sportclub.sportclub.repository;

import com.sportclub.sportclub.entities.Coach;
import com.sportclub.sportclub.entities.Role;

import com.sportclub.sportclub.entities.UserApp;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdminRepo extends JpaRepository<UserApp,Long> {
    Page<UserApp> findByNameContains(String mc, Pageable pageable);
    List<UserApp> findByNameContains(String name);
    UserApp findByEmail(String email);
    Page<UserApp> findByRolesRoleNameContains(String role, Pageable pageable);

}
