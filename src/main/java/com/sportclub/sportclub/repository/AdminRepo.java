package com.sportclub.sportclub.repository;

import com.sportclub.sportclub.entities.Coach;
import com.sportclub.sportclub.entities.Role;
import com.sportclub.sportclub.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdminRepo extends JpaRepository<User,Long> {
    Page<User> findByNameContains(String mc, Pageable pageable);
    List<User> findByNameContains(String name);
    Page<User> findByRolesRoleNameContains(String role, Pageable pageable);

}
