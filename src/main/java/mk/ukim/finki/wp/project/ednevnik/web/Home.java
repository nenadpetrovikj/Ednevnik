package mk.ukim.finki.wp.project.ednevnik.web;

import mk.ukim.finki.wp.project.ednevnik.model.NNSMeeting;
import mk.ukim.finki.wp.project.ednevnik.model.Professor;
import mk.ukim.finki.wp.project.ednevnik.model.Student;
import mk.ukim.finki.wp.project.ednevnik.model.Topic;
import mk.ukim.finki.wp.project.ednevnik.model.enumerations.TopicCategory;
import mk.ukim.finki.wp.project.ednevnik.service.NNSMeetingService;
import mk.ukim.finki.wp.project.ednevnik.service.ProfessorService;
import mk.ukim.finki.wp.project.ednevnik.service.StudentService;
import mk.ukim.finki.wp.project.ednevnik.service.TopicService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping(value = {"/", "/home"})
public class Home {

    private final NNSMeetingService nnsMeetingService;
    private final TopicService topicService;
    private final StudentService studentService;
    private final ProfessorService professorService;

    public Home(NNSMeetingService nnsMeetingService, TopicService topicService, StudentService studentService, ProfessorService professorService) {
        this.nnsMeetingService = nnsMeetingService;
        this.topicService = topicService;
        this.studentService = studentService;
        this.professorService = professorService;
    }

    @GetMapping
    public String getHomePage(Model model, @RequestParam (required = false) List<Topic> filteredList) {
        List<NNSMeeting> nnsMeetings = nnsMeetingService.findAllSortedByDateDesc();
        List<Student> students = studentService.findAll();
        List<Professor> professors = professorService.findAll();

        model.addAttribute("nnsMeetings", nnsMeetings);
        if (filteredList == null) {
            List<Topic> topics = topicService.findAll();
            model.addAttribute("topics", topics);
        } else {
            model.addAttribute("topics", filteredList);
        }

        model.addAttribute("categories", TopicCategory.values());
        model.addAttribute("students", students);
        model.addAttribute("professors", professors);

        return "home";
    }

    @PostMapping
    public String findBySelectedFields(Model model,
                                       @RequestParam Long nnsMeetingId,
                                       @RequestParam String categoryName,
                                       @RequestParam String studentNameAndSurname,
                                       @RequestParam String professorNameAndSurname) {
        model.addAttribute("topics", topicService.findBySelectedFields(nnsMeetingId, categoryName, studentNameAndSurname, professorNameAndSurname));

        return getHomePage(model, topicService.findBySelectedFields(nnsMeetingId, categoryName, studentNameAndSurname, professorNameAndSurname));
    }
}
