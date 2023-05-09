package mk.ukim.finki.wp.project.ednevnik.web;

import mk.ukim.finki.wp.project.ednevnik.model.Professor;
import mk.ukim.finki.wp.project.ednevnik.model.Student;
import mk.ukim.finki.wp.project.ednevnik.model.enumerations.ProfessorRole;
import mk.ukim.finki.wp.project.ednevnik.model.enumerations.TopicCategory;
import mk.ukim.finki.wp.project.ednevnik.model.exceptions.InvalidIdException;
import mk.ukim.finki.wp.project.ednevnik.model.exceptions.NameOrSurnameFieldIsEmptyException;
import mk.ukim.finki.wp.project.ednevnik.service.ProfessorService;
import mk.ukim.finki.wp.project.ednevnik.service.StudentService;
import mk.ukim.finki.wp.project.ednevnik.service.TopicService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/topics")
public class TopicController {

    private final TopicService topicService;
    private final StudentService studentService;
    private final ProfessorService professorService;

    public TopicController(TopicService topicService, StudentService studentService, ProfessorService professorService) {
        this.topicService = topicService;
        this.studentService = studentService;
        this.professorService = professorService;
    }

    @PostMapping
    public String createTopic(@RequestParam TopicCategory categoryName,
                              @RequestParam String subCategoryName,
                              @RequestParam String description,
                              @RequestParam Double serialNumber,
                              @RequestParam Integer isAccepted,
                              @RequestParam String discussion,
                              @RequestParam String studentName,
                              @RequestParam String studentSurname,
                              @RequestParam Long professorId,
                              @RequestParam Long nnsMeetingId,
                              @RequestParam List<Long> professorsIds) {
        try {
            topicService.create(categoryName, subCategoryName, description, serialNumber, isAccepted == 1, discussion, nnsMeetingId, studentName, studentSurname, professorId, professorsIds);
        } catch (NameOrSurnameFieldIsEmptyException e) {
            throw new RuntimeException(e);
        }

        return "redirect:/nns-meetings";
    }


}
