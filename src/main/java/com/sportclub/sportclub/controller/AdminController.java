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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.ArrayList;
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
    @RequestMapping(value = "/adminList", method = RequestMethod.GET)
    public String getAdmins(Model model, @RequestParam(name = "page", defaultValue = "0") int page,
                            @RequestParam(name = "size", defaultValue = "5") int size,
                            @RequestParam(name = "keyword", defaultValue = "") String kw
    ) {


        model.addAttribute("user", new UserApp());



      /*  for (User user:adminService.getAllAdmins()
             ) {
            for (Role role:user.getRoles()
                 ) {
                if (role.getRole_name().equals("Admin") || role.getRole_name().equals("Sub-admin")){*/
        Page<UserApp> pageAdmin;


        pageAdmin = adminService.getUsersByRoles(PageRequest.of(page, size));
        System.out.println("yessss!!!!!!");
        model.addAttribute("users", pageAdmin.getContent());
        model.addAttribute("pages", new int[pageAdmin.getTotalPages()]);
        model.addAttribute("currentPage", page);
        model.addAttribute("keyword", kw);
        UserApp userForm = new UserApp();
        model.addAttribute("userForm", userForm);

        model.addAttribute("user", userForm);
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
    public String addAdmin(@Validated UserApp admin, BindingResult bindingResult, @RequestParam("file") MultipartFile file,
                           RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) return "adminList";

        if (!file.isEmpty()) {
            admin.setPic(file.getOriginalFilename());
            fileStorageService.save(file);
        } else {
            admin.setPic("default-user.png");
        }

        String password = admin.getPassword();
        admin.setPassword(passwordEncoder.encode(password));
        admin.setRoles(roleService.getRoleByid(1L));
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
        redirectAttributes.addFlashAttribute("successMessage", "Utilisateur ajouté avec succès!");


        return "redirect:/adminList";
    }

    @PostMapping("/deleteAdmins")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String deleteCells(@RequestParam("selectedCells") Long[] selectedCells) {
        // Perform the delete operation using the selected cell IDs

        for (Long cellId : selectedCells) {
            UserApp admin = adminService.getAdminById(cellId);
            adminService.deleteAdmin(cellId);

            fileStorageService.deleteFile(admin.getPic());

        }
        // Redirect to a success page or return a response as needed
        return "redirect:/adminList";
    }

    @GetMapping("/adminList/search")
    public String search(@RequestParam("searchBy") String searchBy, @RequestParam("keyword") String keyword, Model model) {
        model.addAttribute("userForm", new UserApp());
        List<UserApp> searchResults = new ArrayList<>();

        if (!keyword.isEmpty()){
            if ("cin".equals(searchBy)) {
                searchResults = adminService.getByCin(keyword);
            } else if ("tele".equals(searchBy)) {
                searchResults = adminService.getByTele(keyword);
            }
        }
        else {
            return "redirect:/adminList";
        }

        model.addAttribute("users", searchResults);
        model.addAttribute("keyword", keyword);
        return "adminList";
    }

    @GetMapping("/deleteAdmin")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String deleteAdmin(@RequestParam(name = "id") Long id, String keyword, int page) {

        UserApp admin = adminService.getAdminById(id);
        adminService.deleteAdmin(id);
        fileStorageService.deleteFile(admin.getPic());

        return "redirect:/adminList?page=" + page + "&keyword=" + keyword;
    }

    @GetMapping("/editAdmin")
    public String editAdmin(@RequestParam(name = "id") Long id, Model model) {
        UserApp userApp = adminService.getAdminById(id);
        model.addAttribute("userApp", userApp);
        model.addAttribute("pic", userApp.getPic());
        model.addAttribute("role", userApp.getRoles().getRole_id());
        model.addAttribute("id", userApp.getId());
        return "updateAdminModal";
    }

    @PostMapping("/editAdmin")
    public String editAdmin(@ModelAttribute(name = "userApp") @Validated UserApp user, BindingResult bindingResult, @RequestParam("file") MultipartFile file) {
        if (bindingResult.hasErrors()) return "error";
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
    public String editAdminProfile(@Validated UserApp c, BindingResult bindingResult, @RequestParam(name = "file") MultipartFile file) {
        if (bindingResult.hasErrors()) return "profile";
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
