package com.sportclub.sportclub.service;

import com.sportclub.sportclub.entities.Role;
import com.sportclub.sportclub.repository.RoleRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class RoleService {
    @Autowired
    RoleRepo roleRepo;
    public List<Role> findAllRoles() {
        return roleRepo.findAll();
    }
    public Role getRoleByid(Long id){
        return roleRepo.findById(id).get();
    }
    public Role getRoleByName(String name){
        return roleRepo.findByRoleName(name);
    }
    public void addRole(Role role){
        roleRepo.save(role);
    }
}

