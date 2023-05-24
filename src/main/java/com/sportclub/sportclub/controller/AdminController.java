package com.sportclub.sportclub.controller;

import com.sportclub.sportclub.entities.*;
import com.sportclub.sportclub.repository.AdminRepo;
import com.sportclub.sportclub.service.AdminService;
import com.sportclub.sportclub.service.RoleService;
import com.sportclub.sportclub.tools.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
public class AdminController {
@Autowired
    RoleService roleService;
@Autowired
    AdminService adminService;
@Autowired
    FileStorageService fileStorageService;
@Autowired
    AdminRepo adminRepo;

    @RequestMapping(value = "/adminList",method = RequestMethod.GET)
    public String getAdmins(Model model, @RequestParam(name = "page", defaultValue = "0") int page,
                             @RequestParam(name = "size", defaultValue = "5") int size,
                             @RequestParam(name = "keyword", defaultValue = "") String kw
    ) {


        model.addAttribute("user", new User());

        List<Role> roles=roleService.findAllRoles();
        model.addAttribute("roles",roles);

      /*  for (User user:adminService.getAllAdmins()
             ) {
            for (Role role:user.getRoles()
                 ) {
                if (role.getRole_name().equals("Admin") || role.getRole_name().equals("Sub-admin")){*/
        Page<User> pageAdmin;

        if ("Admin".equalsIgnoreCase(kw) || "Sub-admin".equalsIgnoreCase(kw)) {
            pageAdmin = adminService.getUsersByRoles(kw, PageRequest.of(page, size));
        } else {
            pageAdmin = adminService.getUsersByRoles("", PageRequest.of(page, size));
        }
                    model.addAttribute("users", pageAdmin.getContent());
                    model.addAttribute("pages", new int[pageAdmin.getTotalPages()]);
                    model.addAttribute("currentPage", page);
                    model.addAttribute("keyword", kw);
                    User userForm = new User();
                    model.addAttribute("userForm", userForm);


        return "adminList";

    }
@GetMapping("/addAdmin")
    public String getAddAdminPage(Model model) {
        User userForm = new User();
        model.addAttribute("userForm", userForm);
        return "adminList";
    }

    @PostMapping("/addAdmin")
    public String addAdmin(@Validated User admin, BindingResult bindingResult, @RequestParam("file") MultipartFile file){
        if(bindingResult.hasErrors()) return "adminList";
        admin.setPic(file.getOriginalFilename());
        adminService.addAdmin(admin);
        fileStorageService.save(file);

        return "redirect:/adminList";
    }
    @PostMapping("/deleteAdmins")
    public String deleteCells(@RequestParam("selectedCells") Long[] selectedCells) {
        // Perform the delete operation using the selected cell IDs

        for (Long cellId : selectedCells) {
            adminService.deleteAdmin(cellId);

        }
            // Redirect to a success page or return a response as needed
            return "redirect:/adminList";
        }

        @RequestMapping(path = {"/adminList/search"})
        public String search (Model model, String user){
            User userForm = new User();
            model.addAttribute("userForm", userForm);
            List<User> list;
            if (user != null) {
                list = adminRepo.findByNameContains(user);
            } else {
                list = adminService.getAllAdmins();
            }
            model.addAttribute("users", list);
            return "adminList";
        }

    @GetMapping("/deleteAdmin")
    public String deleteAdmin(@RequestParam(name = "id") Long id,String keyword, int page){

        adminService.deleteAdmin(id);
        return "redirect:/adminList?page="+page+"&keyword="+keyword;
    }
    }
