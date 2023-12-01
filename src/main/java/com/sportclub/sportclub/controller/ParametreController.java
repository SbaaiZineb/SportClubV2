package com.sportclub.sportclub.controller;

import com.sportclub.sportclub.entities.Abonnement;
import com.sportclub.sportclub.entities.Gym;
import com.sportclub.sportclub.entities.Member;
import com.sportclub.sportclub.entities.Role;
import com.sportclub.sportclub.repository.GymRepo;
import com.sportclub.sportclub.service.GymService;
import com.sportclub.sportclub.tools.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Controller
@PreAuthorize("hasAuthority('ADMIN')")
public class ParametreController {
    @Autowired
    GymRepo gymRepo;
    @Autowired
    GymService gymService;
    @Autowired
    FileStorageService fileStorageService;

    @GetMapping("/parametre")

    public String getPara( Long id, Model model) {
        id=1L;
        List<Role> roles=gymService.getRoles();
        model.addAttribute("roles",roles);
        Gym gym=gymService.getById(id);
        Role roleForm = new Role();
        model.addAttribute("roleForm", roleForm);
        model.addAttribute("gym", gym);
        return "parametre";
    }
    @GetMapping("/addRole")

    public String getaddRole(Model model) {
        Role roleForm = new Role();
        model.addAttribute("roleForm", roleForm);
        return "parametre";
    }
    @PostMapping("/addRole")

    public String addRole(@Validated @ModelAttribute("roleForm") Role roleForm, BindingResult bindingResult,Model model) {
        if(bindingResult.hasErrors()) return "parametre";
        gymService.addRole(roleForm);
        model.addAttribute("added",true);
        return "redirect:/parametre";

    }
    @PostMapping("/deleteRoles")
    public String deleteCells(@RequestParam("selectedab") Long[] selectedab) {
        // Perform the delete operation using the selected cell IDs

        for (Long cellId : selectedab) {

            gymService.deletRole(cellId);
        }

        // Redirect to a success page or return a response as needed
        return "redirect:/parametre";
    }
    @PostMapping("/parametre")
    public String para(@Validated Gym gym, BindingResult bindingResult, @RequestParam("file") MultipartFile file) {
        if (bindingResult.hasErrors()) return "parametre";

        if (file != null && !file.isEmpty()) {

            gym.setLogo(file.getOriginalFilename());
            fileStorageService.save(file);
        } else {

            gymRepo.findById(gym.getId()).ifPresent(existingGym -> gym.setLogo(existingGym.getLogo()));
        }

        gymRepo.save(gym);
        return "redirect:/parametre";
    }
}
