package mk.ukim.finki.wp.project.ednevnik.config;

import mk.ukim.finki.wp.project.ednevnik.model.Professor;
import mk.ukim.finki.wp.project.ednevnik.model.Student;
import mk.ukim.finki.wp.project.ednevnik.model.enumerations.ProfessorRole;
import mk.ukim.finki.wp.project.ednevnik.model.enumerations.TopicCategory;
import mk.ukim.finki.wp.project.ednevnik.model.exceptions.InvalidIdException;
import mk.ukim.finki.wp.project.ednevnik.model.exceptions.NameOrSurnameFieldIsEmptyException;
import mk.ukim.finki.wp.project.ednevnik.service.NNSMeetingService;
import mk.ukim.finki.wp.project.ednevnik.service.ProfessorService;
import mk.ukim.finki.wp.project.ednevnik.service.StudentService;
import mk.ukim.finki.wp.project.ednevnik.service.TopicService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataInitializer {

    private final NNSMeetingService nnsMeetingService;
    private final StudentService studentService;
    private final ProfessorService professorService;
    private final TopicService topicService;


    public DataInitializer(NNSMeetingService nnsMeetingService, StudentService studentService, ProfessorService professorService, TopicService topicService) {
        this.nnsMeetingService = nnsMeetingService;
        this.studentService = studentService;
        this.professorService = professorService;
        this.topicService = topicService;
    }

    private ProfessorRole randomizeProfRole(long i) {
        if (i % 6 == 0) return ProfessorRole.VICE_DEAN;
        if (i % 5 == 0) return ProfessorRole.PROFESSOR;
        if (i % 4 == 0) return ProfessorRole.ASSOCIATE_PROFESSOR;
        if (i % 3 == 0) return ProfessorRole.DOCENT;
        if (i % 2 == 0) return ProfessorRole.ASSISTANT;
        return ProfessorRole.DEMONSTRATOR;
    }

    private Long randomizeNNSMeeting(long i) {
        if (i % 3 == 0) return 3l;
        if (i % 2 == 0) return 2l;
        return 1l;
    }

    @PostConstruct
    public void initData() throws InvalidIdException, NameOrSurnameFieldIsEmptyException {
        for (long i = 1; i < 4; i++) {
            nnsMeetingService.create("meetingCode " + i, LocalDate.now().minusDays(i));
        }
        professorService.create("/", "/", null);
        List<Long> professorIds = new ArrayList<>();
        for (long i = 1; i < 7; i++) {
            Professor professor = professorService.create("Prof.name " + i, "Prof. surname " + i, randomizeProfRole(i));
            professorIds.add(professor.getId());
            Student student = studentService.create("Stud.name " + i, "Stud. surname " + i);
            topicService.create(TopicCategory.values()[(int) (i % 4)], "Subcategory " + i, "Description " + i, i * 1.1, true, "Discussion " + i, randomizeNNSMeeting(i), student.getName(), student.getSurname(), professor.getId(), professorIds);
        }

    }
}
