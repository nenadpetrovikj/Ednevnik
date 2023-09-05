package mk.ukim.finki.wp.project.ednevnik.web;

import mk.ukim.finki.wp.project.ednevnik.model.enumerations.TopicCategory;
import mk.ukim.finki.wp.project.ednevnik.model.exceptions.StudentFormatException;
import mk.ukim.finki.wp.project.ednevnik.service.TopicService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/topics")
public class TopicController {

    private final TopicService topicService;

    public TopicController(TopicService topicService) {
        this.topicService = topicService;
    }

    @PostMapping
    public String createTopic(@RequestParam Long nnsMeetingId,
                              @RequestParam TopicCategory categoryName,
                              @RequestParam String serialNumber,
                              @RequestParam(required = false) Long id,
                              @RequestParam(required = false) String subCategoryName,
                              @RequestParam(required = false) String description,
                              @RequestParam(required = false) Boolean isAccepted,
                              @RequestParam(required = false) String discussion,
                              @RequestParam(required = false) String studentFullNameId,
                              @RequestParam(required = false) Long professorId,
                              @RequestParam(required = false) List<Long> professorsIds) {
        try {
            if (id != null)
                topicService.update(id, categoryName, subCategoryName, description, serialNumber, isAccepted, discussion, nnsMeetingId, studentFullNameId, professorId, professorsIds);
            else
                topicService.create(categoryName, subCategoryName, description, serialNumber, isAccepted, discussion, nnsMeetingId, studentFullNameId, professorId, professorsIds);
        } catch (StudentFormatException e) {
            throw new RuntimeException(e);
        }

        return "redirect:/nns-meetings/" + nnsMeetingId + "/topics-list";
    }

    @GetMapping("/{id}/delete")
    public String deleteTopic(@PathVariable Long id) {
        return "redirect:/nns-meetings/" + topicService.remove(id).getNnsMeeting().getId() + "/topics-list";
    }

}
