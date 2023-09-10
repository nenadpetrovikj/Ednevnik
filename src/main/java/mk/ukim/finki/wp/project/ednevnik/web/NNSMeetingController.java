package mk.ukim.finki.wp.project.ednevnik.web;

import mk.ukim.finki.wp.project.ednevnik.model.NNSMeeting;
import mk.ukim.finki.wp.project.ednevnik.model.Professor;
import mk.ukim.finki.wp.project.ednevnik.model.Topic;
import mk.ukim.finki.wp.project.ednevnik.model.enumerations.TopicCategory;
import mk.ukim.finki.wp.project.ednevnik.service.NNSMeetingService;
import mk.ukim.finki.wp.project.ednevnik.service.ProfessorService;
import mk.ukim.finki.wp.project.ednevnik.service.StudentService;
import mk.ukim.finki.wp.project.ednevnik.service.TopicService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping(value = {"/", "/nns-meetings"})
public class NNSMeetingController {

    private final NNSMeetingService nnsMeetingService;
    private final ProfessorService professorService;
    private final StudentService studentService;
    private final TopicService topicService;

    public NNSMeetingController(NNSMeetingService nnsMeetingService, ProfessorService professorService, StudentService studentService, TopicService topicService) {
        this.nnsMeetingService = nnsMeetingService;
        this.professorService = professorService;
        this.studentService = studentService;
        this.topicService = topicService;
    }

    @GetMapping
    public String getNNSMeetingsPage(Model model,
                              @RequestParam(required = false) List<NNSMeeting> filteredNNSMeetingsDesc,
                              @RequestParam(required = false) LocalDate date,
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
        Page<NNSMeeting> pagedData = nnsMeetingService.findAllWithPagination(pageable);

        if (filteredNNSMeetingsDesc != null && filteredNNSMeetingsDesc.size() == nnsMeetingService.findAllSortedByDateDesc().size()) {
            model.addAttribute("pagedData", pagedData);
        }
        if (!pageString.isEmpty()) {
            model.addAttribute("pagedData", pagedData);
        }

        List<NNSMeeting> nnsMeetingsDesc = nnsMeetingService.findAllSortedByDateDesc();
        if (nnsMeetingsDesc != null && !nnsMeetingsDesc.isEmpty())
            model.addAttribute("latest", nnsMeetingsDesc.get(0));
        if (filteredNNSMeetingsDesc == null) {
            model.addAttribute("nnsMeetings", nnsMeetingsDesc);
            model.addAttribute("selectedDate", null);
        } else {
            model.addAttribute("nnsMeetings", filteredNNSMeetingsDesc);
            model.addAttribute("selectedDate", date);
        }

        model.addAttribute("title", "ННС Седници");
        model.addAttribute("bodyContent", "nns-meetings-page");

        return "master-template";
    }

    @PostMapping
    public String findAllHeldBeforeSelectedDateDesc(Model model, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date, @RequestParam(name = "page", defaultValue = "0") String pageString, @RequestParam(name = "size", defaultValue = "5") int size) {
        return getNNSMeetingsPage(model, nnsMeetingService.findAllHeldBeforeSelectedDateDesc(date), date, "", size);
    }

    @GetMapping("/add")
    public String getNNSMeetingAddPage(Model model) {
        model.addAttribute("title", "Додади Нова Седница");
        model.addAttribute("bodyContent", "nns-meeting-add");
        return "master-template";
    }

    @PostMapping("/add")
    public String createNNSMeeting(@RequestParam String serialCode, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        nnsMeetingService.create(serialCode, date);
        return "redirect:/nns-meetings";

    }

    @GetMapping("/{id}/add-topic")
    public String showAddTopicsPage(Model model, @PathVariable Long id) {
        NNSMeeting nnsMeeting = nnsMeetingService.findById(id);
        List<Professor> professors = professorService.findAll();
        List<String> students = studentService.getAllStudentsInFormat();
        Set<String> subCategories = topicService.getAllSubCategories();

        model.addAttribute("nnsMeeting", nnsMeeting);
        model.addAttribute("professors", professors);
        model.addAttribute("students", students);
        model.addAttribute("student", "");
        model.addAttribute("topicCategories", TopicCategory.values());
        model.addAttribute("subCategories", subCategories);

        model.addAttribute("title", "Додадете Нов Запис");
        model.addAttribute("bodyContent", "add-topic-page");
        return "master-template";
    }

    @GetMapping("/{id}/edit-topic/{topicId}")
    public String getEditTopicPage(Model model, @PathVariable Long id, @PathVariable Long topicId) {
        NNSMeeting nnsMeeting = nnsMeetingService.findById(id);
        List<Professor> professors = professorService.findAll();
        List<String> students = studentService.getAllStudentsInFormat();
        Set<String> subCategories = topicService.getAllSubCategories();
        Topic topic = topicService.findById(topicId);

        String student = "";
        if (topic.getStudent() != null)
            student = topic.getStudent().getName() + " " + topic.getStudent().getSurname() + " " + topic.getStudent().getId();

        model.addAttribute("nnsMeeting", nnsMeeting);
        model.addAttribute("professors", professors);
        model.addAttribute("students", students);
        model.addAttribute("student", student);
        model.addAttribute("topicCategories", TopicCategory.values());
        model.addAttribute("subCategories", subCategories);
        model.addAttribute("topic", topic);

        model.addAttribute("title", "Сменете Запис");
        model.addAttribute("bodyContent", "add-topic-page");
        return "master-template";
    }

    @GetMapping("/{id}/topics-list")
    public String showListTopicsPage(Model model, @PathVariable Long id) {
        NNSMeeting nnsMeeting = nnsMeetingService.findById(id);
        List<Topic> topics = nnsMeetingService.sortTopicsBySerialNumberForNNSMeeting(id);
        model.addAttribute("nnsMeeting", nnsMeeting);
        model.addAttribute("topics", topics);

        model.addAttribute("title", "Преглед На Седница");
        model.addAttribute("bodyContent", "list-topics");
        return "master-template";
    }
}
