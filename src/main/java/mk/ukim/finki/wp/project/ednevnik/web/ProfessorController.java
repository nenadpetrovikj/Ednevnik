package mk.ukim.finki.wp.project.ednevnik.web;

import mk.ukim.finki.wp.project.ednevnik.model.Professor;
import mk.ukim.finki.wp.project.ednevnik.model.enumerations.ProfessorRole;
import mk.ukim.finki.wp.project.ednevnik.service.ProfessorService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/professors")
public class ProfessorController {

    private final ProfessorService professorService;

    public ProfessorController(ProfessorService professorService) {
        this.professorService = professorService;
    }

    @GetMapping
    public String showProfessorsList(Model model) {
        List<Professor> professors = professorService.findAll();
        model.addAttribute("professors", professors);
        return "professors-page";
    }

    @GetMapping("/add")
    public String showProfessorAdd(Model model) {
        model.addAttribute("professorRoles", ProfessorRole.values());
        return "professor-add";
    }

    @GetMapping("/{id}/edit")
    public String showProfessorEdit(@PathVariable Long id, Model model) {
        Professor professor = professorService.findById(id);
        ProfessorRole selectedProfessorRole = professor.getProfessorRole();
        model.addAttribute(professor);
        model.addAttribute("professorRoles", ProfessorRole.values());
        model.addAttribute("selectedProfessorRole", selectedProfessorRole);
        return "professor-edit";
    }

    @PostMapping
    public String professorAdd(@RequestParam String name,
                               @RequestParam String surname,
                               @RequestParam ProfessorRole professorRole) {
        professorService.create(name, surname, professorRole);
        return "redirect:/professors";
    }

    @PostMapping("/{id}")
    public String professorEdit(@PathVariable Long id,
                                @RequestParam String name,
                                @RequestParam String surname,
                                @RequestParam ProfessorRole professorRole) {
        professorService.update(id, name, surname, professorRole);
        return "redirect:/professors";
    }
}
