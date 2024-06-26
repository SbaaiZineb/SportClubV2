package com.sportclub.sportclub.controller;

import com.lowagie.text.DocumentException;
import com.sportclub.sportclub.entities.*;
import com.sportclub.sportclub.repository.CoachCheckInRepo;
import com.sportclub.sportclub.repository.CoachRepository;
import com.sportclub.sportclub.service.CoachService;
import com.sportclub.sportclub.service.RoleService;
import com.sportclub.sportclub.service.SeanceService;
import com.sportclub.sportclub.service.SportService;
import com.sportclub.sportclub.tools.CoachPdf;
import com.sportclub.sportclub.tools.FileStorageService;
import com.sportclub.sportclub.tools.MemberPdf;
import jakarta.servlet.http.HttpServletResponse;
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

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@PreAuthorize("hasAuthority('ADMIN')")
public class CoachController {
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    CoachService coachService;
    @Autowired
    SeanceService seanceService;
    @Autowired
    FileStorageService fileStorageService;
    @Autowired
    CoachRepository coachRepository;
    @Autowired
    RoleService roleService;
    @Autowired
    SportService sportService;
    @Autowired
    CoachCheckInRepo coachCheckInRepo;

    @GetMapping("/coachList")
    @PreAuthorize("hasAuthority('ADMIN')")

    public String getCoachs(Model model , @RequestParam(name = "page",defaultValue = "0") int page,
                             @RequestParam(name = "size",defaultValue = "5") int size,
                             @RequestParam(name = "keyword",defaultValue = "") String kw
    ) {
        Page<Coach> pageCoach = coachService.findByCoachName(kw, PageRequest.of(page,size));
        model.addAttribute("listCoach",pageCoach.getContent());
        model.addAttribute("pages",new int[pageCoach.getTotalPages()]);
        model.addAttribute("currentPage",page);
        model.addAttribute("keyword",kw);
        Coach CoachForm = new Coach();
        model.addAttribute("CoachForm", CoachForm);
        List<Sport> sports=sportService.getAllSports();
        model.addAttribute("sports",sports);
        return "coachList";

    }
    @PostMapping("/deleteCoachs")
    @PreAuthorize("hasAuthority('ADMIN') ")

    public String deleteCells(@RequestParam(value = "selectedCells",required = false) Long[] selectedCells,RedirectAttributes redirectAttributes) {
        // Perform the delete operation using the selected cell IDs
        if (selectedCells != null && selectedCells.length > 0) {
        for (Long cellId : selectedCells) {


            List<Seance> seances=seanceService.getSeanceByCoach(cellId);
            for (Seance seance:seances) {
                seance.setCoach(null);
            }

            Coach coach = coachService.getCoachById(cellId);
            coachService.deleteCoach(cellId);
            fileStorageService.deleteFile(coach.getPic());
        }
            redirectAttributes.addFlashAttribute("successMessage", "Entraineurs supprimés avec succès!");
        } else {
            // No checkboxes were selected, handle accordingly (e.g., show an error message)
            redirectAttributes.addFlashAttribute("errorMessage", "Aucune cellule sélectionnée pour suppression.");
        }
        // Redirect to a success page or return a response as needed
        return "redirect:/coachList";
    }

    @GetMapping("/coachList/search")
    public String search(@RequestParam("keyword") String keyword, Model model) {
        model.addAttribute("CoachForm", new Coach());
        List<Coach> searchResults;

        if (!keyword.isEmpty()) {
            searchResults = coachService.findByKeyword(keyword);
            model.addAttribute("listCoach", searchResults);
        } else {
            return "redirect:/coachList";
        }
        model.addAttribute("keyword", keyword);

        return "coachList";
    }

    @GetMapping("/addCoach")
    @PreAuthorize("hasAuthority('ADMIN')")

    public String getAddCoachPage(Model model) {
        Coach coachForm = new Coach();
        model.addAttribute("CoachForm", coachForm);
        return "coachList";
    }

    @PostMapping("/addCoach")
    public String addCoach(@Validated Coach c, BindingResult bindingResult, @RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes){
        if(bindingResult.hasErrors()) return "error";
        if (!file.isEmpty()){
            c.setPic(file.getOriginalFilename());
            fileStorageService.save(file);
        }else {
            c.setPic("default-user.png");
        }


        String password= c.getPassword();
        if(password!=null){
            c.setPassword(passwordEncoder.encode(password));

        }
        List<Role> roles=roleService.findAllRoles();

        for (Role role:roles
        ) {
            if (role.getRoleName().equals("COACH")){

                c.setRoles(role);
            }
        }
        coachService.addCoach(c);

        redirectAttributes.addFlashAttribute("successMessage", "Coach ajouté avec succès!");


        return "redirect:/coachList";
    }
    /*@RequestMapping(value = {"/addCoach"}, method = RequestMethod.POST)
    public String saveCoach(Model model,
                               @ModelAttribute("CoachForm") Coach CoachForm) {
        String name = CoachForm.getName();
        String lname = CoachForm.getLname();
        String email = CoachForm.getEmail();
        String adress = CoachForm.getAdress();
        String cin = CoachForm.getCin();
        LocalDate dob = CoachForm.getDob();
        int tele = CoachForm.getTele();
        String password = CoachForm.getPassword();
        String sport_Type = CoachForm.getSport_type();
        List<Role> roles=CoachForm.getRoles();

        if (name != null && name.length() > 0 && lname != null && lname.length() > 0) {
            Coach newCoach = new Coach(name, lname,adress,cin,   dob,  tele,roles,email, password, sport_Type);
            coachService.addCoach(newCoach);
            return "redirect:/coachList";
        }
        model.addAttribute("errorMessage", "Il faut saisir toutes les information");
        return "coachList";
    }*/
    @GetMapping("/deleteCoach")
    public String deleteCoach(@RequestParam(name = "id") Long id){
        List<Seance> seances=seanceService.getSeanceByCoach(id);
        List<CheckInCoach> checkInCoach= coachCheckInRepo.getCheckInByCoach(coachService.getCoachById(id));
        for (Seance seance:seances) {
            seance.setCoach(null);

        }
        for (CheckInCoach checkIn:checkInCoach
             ) {
            checkIn.setCoach(null);
        }
        Coach coach = coachService.getCoachById(id);
        coachService.deleteCoach(id);
        fileStorageService.deleteFile(coach.getPic());
        return "redirect:/coachList";
    }
    @GetMapping("/editCoach")
    @PreAuthorize("hasAuthority('ADMIN')")

    public String editCoach(@RequestParam(name = "id") Long id, Model model){
        Coach coach=coachService.getCoachById(id);
        List<Sport> sports=sportService.getAllSports();
        model.addAttribute("sports",sports);
        model.addAttribute("coach",coach);
        return "updateCoachModal";
    }

    @PostMapping("/editCoach")
    public String editCoach(@Validated Coach c, BindingResult bindingResult, @RequestParam("file") MultipartFile file){
        if(bindingResult.hasErrors()) return "error";
        Coach existingCoach = coachService.getCoachById(c.getId());
        if (file != null && !file.isEmpty()) {

            c.setPic(file.getOriginalFilename());
            fileStorageService.save(file);
        } else {

            if (existingCoach != null) {
                c.setPic(existingCoach.getPic());
            }
        }
        coachService.updateCoach(c);


        return "redirect:/coachList";
    }

    @GetMapping("coachList/export/pdf")
    public void exportToPDF(HttpServletResponse response) throws DocumentException, IOException {
        response.setContentType("application/pdf");


        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=coach_" + currentDateTime + ".pdf";
        response.setHeader(headerKey, headerValue);

        List<Coach> listUsers = coachService.getAllCoachs();
        CoachPdf exporter = new CoachPdf(listUsers);
        exporter.export(response);
    }

}
