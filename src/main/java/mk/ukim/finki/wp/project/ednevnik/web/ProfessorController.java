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
        List<Professor> professors = professorService.findAll().subList(1, professorService.findAll().size()); // to not include the first professor "/"
        model.addAttribute("professors", professors);
        return "professors-page";
    }

    @GetMapping("/add")
    public String showProfessorAdd(Model model) {
        String description = "Add professor";
        model.addAttribute("description", description);
        model.addAttribute("professorRoles", ProfessorRole.values());
        return "professor-add";
    }

    @GetMapping("/{id}/edit")
    public String showProfessorEdit(@PathVariable Long id, Model model) {
        Professor professor = professorService.findById(id);
        String description = "Edit professor";
        model.addAttribute("description", description);
        model.addAttribute(professor);
        model.addAttribute("professorRoles", ProfessorRole.values());
        return "professor-add";
    }

    @PostMapping
    public String professorAdd(@RequestParam(required = false) Long id,
                               @RequestParam String name,
                               @RequestParam String surname,
                               @RequestParam ProfessorRole professorRole) {
        if (id != null)
            professorService.update(id, name, surname, professorRole);
        else
            professorService.create(name, surname, professorRole);
        return "redirect:/professors";
    }
}
