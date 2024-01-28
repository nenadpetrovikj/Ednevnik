package mk.ukim.finki.wp.project.ednevnik.web;

import mk.ukim.finki.wp.project.ednevnik.model.Student;
import mk.ukim.finki.wp.project.ednevnik.model.Topic;
import mk.ukim.finki.wp.project.ednevnik.model.enumerations.TopicCategory;
import mk.ukim.finki.wp.project.ednevnik.model.exceptions.StudentFormatException;
import mk.ukim.finki.wp.project.ednevnik.service.ProfessorService;
import mk.ukim.finki.wp.project.ednevnik.service.StudentService;
import mk.ukim.finki.wp.project.ednevnik.service.TopicService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;

    private final TopicService topicService;

    private final ProfessorService professorService;

    public StudentController(StudentService studentService, TopicService topicService, ProfessorService professorService) {
        this.studentService = studentService;
        this.topicService = topicService;
        this.professorService = professorService;
    }


    @GetMapping
    public String showStudentList(Model model) {
        model.addAttribute("students", studentService.findAll().stream().map(student -> student.getName() + ' ' + student.getSurname() + ' ' + student.getId()).toList());
//        model.addAttribute("students", studentService.findAllAsString());
        model.addAttribute("title", "Студенти");
        model.addAttribute("bodyContent", "students-page");
        return "master-template";
    }

    @PostMapping
    public String showTopicsForStudent(Model model, @RequestParam String studentFullNameId) throws StudentFormatException {
        Student student = studentService.checkFormatAndReturnStudent(studentFullNameId);
        List<Topic> topicsForStudent;

        if (student != null) {
            topicsForStudent = studentService.topicsForThisStudentSortedByTheirNNSMeetingDate(student);
            model.addAttribute("student", student);
            model.addAttribute("isStudentString", false);
            model.addAttribute("topicCategories", TopicCategory.values());
            model.addAttribute("subCategories", topicService.getAllSubCategories());
            model.addAttribute("professors", professorService.findAll());
        }
        else {
            topicsForStudent = List.of();
            model.addAttribute("student", studentFullNameId);
            model.addAttribute("isStudentString", true);
        }

        model.addAttribute("topics", topicsForStudent);

        return showStudentList(model);
    }

    @PostMapping("/{id}/topics-list")
    public String showTopicsForStudentFilteredBySpecs(Model model,
                                                      @PathVariable Long id,
                                                      @RequestParam (required = false) String categoryName,
                                                      @RequestParam(required = false) String subCategoryName,
                                                      @RequestParam (required = false) Long professorId) {
        Student student = studentService.findById(id);
        List<Topic> topics = studentService.topicsForThisStudentFilteredBySpecs(student, categoryName, subCategoryName, professorId);
        model.addAttribute("topics", topics);
        if (!categoryName.equalsIgnoreCase("Сите"))
            model.addAttribute("selectedCat", TopicCategory.valueOf(categoryName));
        model.addAttribute("selectedSubCat", subCategoryName);
        model.addAttribute("selectedProf", professorId);
        model.addAttribute("student", student);
        model.addAttribute("isStudentString", false);
        model.addAttribute("topicCategories", TopicCategory.values());
        model.addAttribute("subCategories", topicService.getAllSubCategories());
        model.addAttribute("professors", professorService.findAll());
        return showStudentList(model);
    }

    @GetMapping("/add")
    public String showStudentAdd(Model model) {
        model.addAttribute("title", "Додадете Нов Студент");
        model.addAttribute("bodyContent", "student-add");
        return "master-template";
    }

    @GetMapping("/{id}/edit")
    public String showStudentEdit(@PathVariable Long id, Model model) {
        Student student = studentService.findById(id);
        model.addAttribute(student);
        model.addAttribute("title", "Сменете Студент");
        model.addAttribute("bodyContent", "student-add");
        return "master-template";
    }

    @PostMapping("/make-changes")
    public String studentAdd(@RequestParam(required = false) Long idOld,
                             @RequestParam Long idNew,
                             @RequestParam String name,
                             @RequestParam String surname) {
        if (idOld != null) {
            studentService.update(idOld, idNew, name, surname);
        } else {
            try {
                studentService.create(name + ' ' + surname + ' ' + idNew);
            } catch (StudentFormatException e) {
                throw new RuntimeException(e);
            }
        }
        return "redirect:/students";
    }

    @GetMapping("/{id}/delete")
    public String studentDelete(@PathVariable Long id) {
        studentService.remove(id);
        return "redirect:/students";
    }

}
