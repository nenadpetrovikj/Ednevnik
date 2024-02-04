package mk.ukim.finki.wp.project.ednevnik.web;

import mk.ukim.finki.wp.project.ednevnik.model.NNSMeeting;
import mk.ukim.finki.wp.project.ednevnik.model.Professor;
import mk.ukim.finki.wp.project.ednevnik.model.Topic;
import mk.ukim.finki.wp.project.ednevnik.model.enumerations.ProfessorRole;
import mk.ukim.finki.wp.project.ednevnik.model.enumerations.TopicCategory;
import mk.ukim.finki.wp.project.ednevnik.model.exceptions.StudentFormatException;
import mk.ukim.finki.wp.project.ednevnik.service.ProfessorService;
import mk.ukim.finki.wp.project.ednevnik.service.StudentService;
import mk.ukim.finki.wp.project.ednevnik.service.TopicService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/professors")
public class ProfessorController {

    private final ProfessorService professorService;
    private final TopicService topicService;

    private final StudentService studentService;

    public ProfessorController(ProfessorService professorService, TopicService topicService, StudentService studentService) {
        this.professorService = professorService;
        this.topicService = topicService;
        this.studentService = studentService;
    }

    @GetMapping
    public String showProfessorsList(Model model,
                                     @RequestParam(required = false) Professor chosenProf,
                                     @RequestParam(required = false) List<Topic> topicsThatIncludeChosenProf,
                                     @RequestParam(name = "page", defaultValue = "0") String pageString,
                                     @RequestParam(name = "size", defaultValue = "5") int size) {

        int page;
        try {
            page = Integer.parseInt(pageString);
        } catch (NumberFormatException e) {
            // Handle invalid page parameter here, e.g., set a default value or return an error message
            page = 0;
        }
        PageRequest pageable = PageRequest.of(page, size);
        Page<Professor> pagedData = professorService.findAllWithPagination(pageable);

        if (chosenProf == null) {
            model.addAttribute("pagedData", pagedData);
        }
        if (!pageString.isEmpty()) {
            model.addAttribute("pagedData", pagedData);
        }

        boolean showTopics = false;
        List<Professor> professorsInFilter = professorService.findAll();
        List<Professor> professorsToBeShown = professorService.findAll();

        if (topicsThatIncludeChosenProf != null) {
            showTopics = true;
            model.addAttribute("topics", topicsThatIncludeChosenProf);
            model.addAttribute("topicCategories", TopicCategory.values());
            model.addAttribute("subCategories", topicService.getAllSubCategories());
            model.addAttribute("students", studentService.getAllStudentsInFormat());
        }

        if (chosenProf == null) {
            model.addAttribute("professorsToBeShown", professorsToBeShown);
        } else {
            model.addAttribute("professorsToBeShown", professorsToBeShown.stream().filter(professor -> professor.equals(chosenProf)).toList());
        }

        model.addAttribute("chosenProf", chosenProf);
        model.addAttribute("professorsInFilter", professorsInFilter);

        model.addAttribute("showTopics", showTopics);

        model.addAttribute("title", "Професори");
        model.addAttribute("bodyContent", "professors-page");
        return "master-template";
    }

    @PostMapping
    public String filterByProfessor(Model model, @RequestParam Long filterByProfessor) {
        return showProfessorsList(model, professorService.findById(filterByProfessor), null, "", 5);
    }

    @GetMapping("/{id}/topics-list")
    public String showTopicsForProfessor(Model model, @PathVariable Long id) {
        Professor chosenProf = professorService.findById(id);
        return showProfessorsList(model, chosenProf, professorService.topicsForThisProfSortedByTheirNNSMeetingDate(chosenProf), "", 5);
    }

    @PostMapping("/{id}/topics-list")
    public String showTopicsForProfessorFilteredBySpecs(Model model,
                                                        @PathVariable Long id,
                                                        @RequestParam (required = false) String categoryName,
                                                        @RequestParam(required = false) String subCategoryName,
                                                        @RequestParam(required = false) String studentFullNameId) throws StudentFormatException {
        Professor chosenProf = professorService.findById(id);
        if (!categoryName.equalsIgnoreCase("Сите"))
            model.addAttribute("selectedCat", TopicCategory.valueOf(categoryName));
        model.addAttribute("selectedSubCat", subCategoryName);
        model.addAttribute("selectedStudent", studentFullNameId);
        return showProfessorsList(model, chosenProf, professorService.topicsForThisProfessorFilteredBySpecs(chosenProf, categoryName, subCategoryName, studentFullNameId), "", 5);
    }

    @GetMapping("/add")
    public String showProfessorAdd(Model model) {
        model.addAttribute("professorRoles", ProfessorRole.values());
        model.addAttribute("title", "Додадете Нов Професор");
        model.addAttribute("bodyContent", "professor-add");
        return "master-template";
    }

    @GetMapping("/{id}/edit")
    public String showProfessorEdit(Model model, @PathVariable Long id) {
        Professor professor = professorService.findById(id);
        model.addAttribute(professor);
        model.addAttribute("professorRoles", ProfessorRole.values());
        model.addAttribute("title", "Сменете Професор");
        model.addAttribute("bodyContent", "professor-add");
        return "master-template";
    }

    @PostMapping("/make-changes")
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

    @GetMapping("/{id}/delete")
    public String deleteProf(@PathVariable Long id) {
        professorService.remove(id);
        return "redirect:/professors";
    }
}
