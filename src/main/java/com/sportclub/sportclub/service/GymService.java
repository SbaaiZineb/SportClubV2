/*
package com.sportclub.sportclub.service;

import com.sportclub.sportclub.entities.Gym;
import com.sportclub.sportclub.entities.Member;
import com.sportclub.sportclub.entities.Role;
import com.sportclub.sportclub.repository.GymRepo;
import com.sportclub.sportclub.repository.RoleRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GymService {
 @Autowired
    GymRepo gymRepo;
 @Autowired
    RoleRepo roleRepo;
    public Gym getById(Long id) {
        return gymRepo.findById(id).get();
    }
    public Role getRoleById(Long id) {
        return roleRepo.findById(id).get();
    }
    public List<Role> getRoles(){
        return roleRepo.findAll();
    }
    public void addRole(Role role){
        roleRepo.save(role);
    }
    public void deletRole(Long id){
        roleRepo.deleteById(id);
    }
}
*/
