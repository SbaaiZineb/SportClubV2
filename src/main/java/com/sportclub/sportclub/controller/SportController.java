package com.sportclub.sportclub.controller;

import com.sportclub.sportclub.entities.Sport;
import com.sportclub.sportclub.service.CoachService;
import com.sportclub.sportclub.service.SportService;
import com.sportclub.sportclub.tools.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
public class SportController {
    @Autowired
    SportService service;
    @Autowired
    FileStorageService fileStorageService;
    @GetMapping("/sportList")

    public String getSport(Model model , @RequestParam(name = "page",defaultValue = "0") int page,
                         @RequestParam(name = "size",defaultValue = "7") int size,
                         @RequestParam(name = "keyword",defaultValue = "") String kw
    ) {

        Page<Sport> pageAb = service.findByName(kw, PageRequest.of(page,size));
        model.addAttribute("listSport",pageAb.getContent());
        model.addAttribute("pages",new int[pageAb.getTotalPages()]);
        model.addAttribute("currentPage",page);
        model.addAttribute("keyword",kw);


        Sport sport = new Sport();
        model.addAttribute("sport", sport);
        return "sportList";

    }
    @PostMapping("/deleteSports")
    public String deleteCells(@RequestParam("selectedS") Long[] selectedS) {
        // Perform the delete operation using the selected cell IDs

        for (Long cellId : selectedS) {

            service.deleteSport(cellId);
        }

        // Redirect to a success page or return a response as needed
        return "redirect:/sportList";
    }
    @RequestMapping(path = {"/sportList/search"})
    public String search( Model model, String sport) {

        if(sport!=null) {
            List<Sport> list = service.findByNameCon(sport);
            model.addAttribute("listSport", list);
        }else {
            List<Sport> list = service.getAllSports();
            model.addAttribute("listSport", list);}
        return "sportList";
    }
  /*  @GetMapping("/abonnementList/getMembers")
    public String getMembersByMembership(@RequestParam(name = "id") Long id,Model model){
        List<Member> members=memberService.getMemberByMembership(id);
        model.addAttribute("members",members);
        return "MListByAb";
    }*/
    @GetMapping("/addSport")
    public String getAddSprot(Model model) {
        Sport sport = new Sport();
        model.addAttribute("sport", sport);
        return "sportList";
    }
    @PostMapping("/addSport")
    public String addAb(@Validated Sport sport, BindingResult bindingResult, @RequestParam("file") MultipartFile file){
        if(bindingResult.hasErrors()) return "sportList";
        sport.setPic(file.getOriginalFilename());
        fileStorageService.save(file);
        service.addSport(sport);
        return "redirect:/sportList";
    }


@Autowired
    CoachService coachService;
    @GetMapping("/deleteSport")
    public String deleteSp(@RequestParam(name = "id") Long id,String keyword, int page){

        service.deleteSport(id);
        return "redirect:/sportList?page="+page+"&keyword="+keyword;
    }
    @GetMapping("/editSport")

    public String editSport(@RequestParam(name = "id") Long id, Model model){
        Sport ab=service.getSportById(id);
        model.addAttribute("sport",ab);
        return "EditSportModal";
    }

    @PostMapping("/editSport")
    public String editSport(@Validated Sport sport, BindingResult bindingResult){
        if(bindingResult.hasErrors()) return "EditSportModal";

        service.updateSport(sport);
        return "redirect:/sportList";
    }
}
