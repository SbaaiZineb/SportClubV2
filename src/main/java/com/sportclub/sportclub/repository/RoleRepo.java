package com.sportclub.sportclub.repository;

import com.sportclub.sportclub.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepo extends JpaRepository<Role,Long> {
    Role findByRoleName(String role);
}
