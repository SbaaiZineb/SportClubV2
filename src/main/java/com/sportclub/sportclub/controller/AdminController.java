package com.sportclub.sportclub.controller;

import com.sportclub.sportclub.entities.*;
import com.sportclub.sportclub.repository.AdminRepo;
import com.sportclub.sportclub.service.AdminService;
import com.sportclub.sportclub.service.RoleService;
import com.sportclub.sportclub.tools.FileStorageService;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Controller
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminController {
    @Autowired
    PasswordEncoder passwordEncoder;
@Autowired
    RoleService roleService;
@Autowired
    AdminService adminService;
@Autowired
    FileStorageService fileStorageService;
@Autowired
    AdminRepo adminRepo;
    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "/adminList",method = RequestMethod.GET)
    public String getAdmins(Model model, @RequestParam(name = "page", defaultValue = "0") int page,
                             @RequestParam(name = "size", defaultValue = "5") int size,
                             @RequestParam(name = "keyword", defaultValue = "") String kw
    ) {


        model.addAttribute("user", new UserApp());

        List<Role> roles=roleService.findAllRoles();
        model.addAttribute("roles",roles);

      /*  for (User user:adminService.getAllAdmins()
             ) {
            for (Role role:user.getRoles()
                 ) {
                if (role.getRole_name().equals("Admin") || role.getRole_name().equals("Sub-admin")){*/
        Page<UserApp> pageAdmin;
        kw="ADMIN";
        pageAdmin = adminService.getUsersByRoles(kw, PageRequest.of(page, size));
        System.out.println("yessss!!!!!!");
        model.addAttribute("users", pageAdmin.getContent());
                    model.addAttribute("pages", new int[pageAdmin.getTotalPages()]);
                    model.addAttribute("currentPage", page);
                    model.addAttribute("keyword", kw);
                    UserApp userForm = new UserApp();
                    model.addAttribute("userForm", userForm);

model.addAttribute("user",userForm);
        return "adminList";

    }
@GetMapping("/addAdmin")
@PreAuthorize("hasAuthority('ADMIN')")
    public String getAddAdminPage(Model model) {
        UserApp userForm = new UserApp();
        model.addAttribute("userForm", userForm);
        return "adminList";
    }

    @PostMapping("/addAdmin")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String addAdmin(@Validated UserApp admin, BindingResult bindingResult, @RequestParam("file") MultipartFile file){
        if(bindingResult.hasErrors()) return "adminList";

        admin.setPic(file.getOriginalFilename());

        String password= admin.getPassword();
        admin.setPassword(passwordEncoder.encode(password));
       /* String name = admin.getLname();
        String lname = admin.getLname();
        String email = admin.getEmail();
        String adress = admin.getAdress();
        String cin = admin.getCin();
        LocalDate dob = admin.getDob();
        int tele = admin.getTele();
        String password = admin.getPassword();
        Role role=admin.getRoles();
        UserApp admin=new UserApp(,name,lname,email,adress,cin,dob,tele,password,role);*/
        adminService.addAdmin(admin);
        if (!file.isEmpty()){
            fileStorageService.save(file);
        }


        return "redirect:/adminList";
    }
    @PostMapping("/deleteAdmins")
    @PreAuthorize("hasAuthority('ADMIN')")
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
            UserApp userForm = new UserApp();
            model.addAttribute("userForm", userForm);
            List<UserApp> list;
            if (user != null) {
                list = adminRepo.findByNameContains(user);
            } else {
                list = adminService.getAllAdmins();
            }
            model.addAttribute("users", list);
            return "adminList";
        }

    @GetMapping("/deleteAdmin")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String deleteAdmin(@RequestParam(name = "id") Long id,String keyword, int page){

        adminService.deleteAdmin(id);
        return "redirect:/adminList?page="+page+"&keyword="+keyword;
    }
    @GetMapping("/editAdmin")
    public String editAdmin(@RequestParam(name = "id") Long id, Model model){
        UserApp userApp=adminService.getAdminById(id);
        model.addAttribute("userApp",userApp);
        model.addAttribute("pic",userApp.getPic());
        model.addAttribute("role",userApp.getRoles().getRole_id());
        model.addAttribute("id",userApp.getId());
        return "updateAdminModal";
    }

    @PostMapping("/editAdmin")
    public String editAdmin(@ModelAttribute(name = "userApp") @Validated UserApp user, BindingResult bindingResult,@RequestParam("file") MultipartFile file){
        if(bindingResult.hasErrors()) return "error";
        UserApp existingAdmin = adminService.getAdminById(user.getId());
        if (file != null && !file.isEmpty()) {

            user.setPic(file.getOriginalFilename());
            fileStorageService.save(file);
        } else {

            if (existingAdmin != null) {
                user.setPic(existingAdmin.getPic());
            }
        }
        adminService.updateAdmin(user);
        return "redirect:/adminList";
    }
    @PostMapping("/editAdminProfile")
    public String editAdminProfile(@Validated UserApp c, BindingResult bindingResult,@RequestParam(name = "file") MultipartFile file){
        if(bindingResult.hasErrors()) return "profile";
        UserApp existingAdmin = adminService.getAdminById(c.getId());
        if (file != null && !file.isEmpty()) {

            c.setPic(file.getOriginalFilename());
            fileStorageService.save(file);
        } else {

            if (existingAdmin != null) {
                c.setPic(existingAdmin.getPic());
            }
        }
        adminService.updateAdmin(c);

        return "redirect:/profil";
    }
    }
