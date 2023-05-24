package com.sportclub.sportclub.service;

import com.sportclub.sportclub.entities.Role;
import com.sportclub.sportclub.repository.RoleRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class RoleServiceImp implements RoleService{
    @Autowired
    RoleRepo roleRepo;
    @Override
    public List<Role> findAllRoles() {
        return roleRepo.findAll();
    }
}
