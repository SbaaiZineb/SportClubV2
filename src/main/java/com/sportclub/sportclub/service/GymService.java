package com.sportclub.sportclub.service;

import com.sportclub.sportclub.entities.Gym;
import com.sportclub.sportclub.entities.Role;
import com.sportclub.sportclub.repository.GymRepo;
import com.sportclub.sportclub.repository.RoleRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GymService {
 @Autowired
    GymRepo gymRepo;
 @Autowired
    RoleRepo roleRepo;
 public void addGymInfo(Gym gym){
     gymRepo.save(gym);
 }
    public Gym getById(Long id) {
        Optional<Gym> optionalGym = gymRepo.findById(id);
        //if not present return null
        return optionalGym.orElse(null);
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
