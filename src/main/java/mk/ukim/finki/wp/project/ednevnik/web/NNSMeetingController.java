package mk.ukim.finki.wp.project.ednevnik.web;

import mk.ukim.finki.wp.project.ednevnik.model.NNSMeeting;
import mk.ukim.finki.wp.project.ednevnik.model.Professor;
import mk.ukim.finki.wp.project.ednevnik.model.Topic;
import mk.ukim.finki.wp.project.ednevnik.model.enumerations.TopicCategory;
import mk.ukim.finki.wp.project.ednevnik.service.NNSMeetingService;
import mk.ukim.finki.wp.project.ednevnik.service.ProfessorService;
import mk.ukim.finki.wp.project.ednevnik.service.TopicService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/nns-meetings")
public class NNSMeetingController {

    private final NNSMeetingService nnsMeetingService;
    private final ProfessorService professorService;
    private final TopicService topicService;

    public NNSMeetingController(NNSMeetingService nnsMeetingService, ProfessorService professorService, TopicService topicService) {
        this.nnsMeetingService = nnsMeetingService;
        this.professorService = professorService;
        this.topicService = topicService;
    }

    @GetMapping
    public String getNNSMeetingsPage(Model model) {
        List<NNSMeeting> nnsMeetings = nnsMeetingService.findAllSortedByDateDesc();
        model.addAttribute("nnsMeetings", nnsMeetings);
        return "nns-meetings-page";
    }

    @GetMapping("/add")
    public String getNNSMeetingAddPage() {
        return "nns-meeting-add";
    }

    @PostMapping
    public String createNNSMeeting(@RequestParam String serialCode, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        nnsMeetingService.create(serialCode, date);
        return "redirect:/nns-meetings";

    }

    @GetMapping("/{id}/add-topic")
    public String showAddTopicsPage(Model model, @PathVariable Long id) {
        NNSMeeting nnsMeeting = nnsMeetingService.findById(id);
        List<Professor> professors = professorService.findAll();
        model.addAttribute("nnsMeeting", nnsMeeting);
        model.addAttribute("professors", professors);
        model.addAttribute("topicCategories", TopicCategory.values());
        return "add-topic-page";
    }

    @GetMapping("/{id}/topics-list")
    public String showListTopicsPage(Model model, @PathVariable Long id) {
        NNSMeeting nnsMeeting = nnsMeetingService.findById(id);
        List<Topic> topics = nnsMeeting.getTopics();
        topics = topicService.sortTopicsBySerialNumber(topics);
        model.addAttribute("nnsMeeting", nnsMeeting);
        model.addAttribute("topics", topics);
        return "list-topics";
    }
}
