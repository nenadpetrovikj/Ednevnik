package mk.ukim.finki.wp.project.ednevnik.service.implementation;

import mk.ukim.finki.wp.project.ednevnik.model.NNSMeeting;
import mk.ukim.finki.wp.project.ednevnik.model.Professor;
import mk.ukim.finki.wp.project.ednevnik.model.Student;
import mk.ukim.finki.wp.project.ednevnik.model.Topic;
import mk.ukim.finki.wp.project.ednevnik.model.enumerations.TopicCategory;
import mk.ukim.finki.wp.project.ednevnik.model.exceptions.StudentFormatException;
import mk.ukim.finki.wp.project.ednevnik.repository.ProfessorRepository;
import mk.ukim.finki.wp.project.ednevnik.repository.TopicRepository;
import mk.ukim.finki.wp.project.ednevnik.service.NNSMeetingService;
import mk.ukim.finki.wp.project.ednevnik.service.ProfessorService;
import mk.ukim.finki.wp.project.ednevnik.service.StudentService;
import mk.ukim.finki.wp.project.ednevnik.service.TopicService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TopicServiceImplementation implements TopicService {

    private final TopicRepository topicRepository;
    private final NNSMeetingService nnsMeetingService;
    private final StudentService studentService;
    private final ProfessorService professorService;
    private final ProfessorRepository professorRepository;

    public TopicServiceImplementation(TopicRepository topicRepository, NNSMeetingService nnsMeetingService, StudentService studentService, ProfessorService professorService, ProfessorRepository professorRepository) {
        this.topicRepository = topicRepository;
        this.nnsMeetingService = nnsMeetingService;
        this.studentService = studentService;
        this.professorService = professorService;
        this.professorRepository = professorRepository;
    }

    @Override
    public List<Topic> findAll() {
        return topicRepository.findAll();
    }

    @Override
    public Topic findById(Long id) {
        return topicRepository.findById(id).orElseGet(() -> null);
    }

    @Override
    public Topic create(TopicCategory categoryName, String subCategoryName, String description, String serialNumber, Boolean isAccepted, String discussion, Long nnsMeetingId, String studentFullNameId, Long professorId, List<Long> professorIds) throws StudentFormatException {
        NNSMeeting nnsMeeting = nnsMeetingService.findById(nnsMeetingId);
        Student student = studentService.create(studentFullNameId);
        Professor professor = professorService.findById(professorId);

        List<Professor> professors = null;
        if (professorIds != null && !professorIds.contains(-1))
            professors = professorRepository.findAllById(professorIds);

        return topicRepository.save(new Topic(categoryName, subCategoryName, description, serialNumber, isAccepted, discussion, nnsMeeting, student, professor, professors));
    }

    @Override
    public Topic update(Long id, TopicCategory categoryName, String subCategoryName, String description, String serialNumber, Boolean isAccepted, String discussion, Long nnsMeetingId, String studentFullNameId, Long professorId, List<Long> professorIds) throws StudentFormatException {
        NNSMeeting nnsMeeting = nnsMeetingService.findById(nnsMeetingId);
        Student student = studentService.create(studentFullNameId);
        Professor professor = professorService.findById(professorId);

        List<Professor> professors = null;
        if (professorIds != null && !professorIds.contains(-1))
            professors = professorRepository.findAllById(professorIds);

        Topic topic = findById(id);

        topic.setCategoryName(categoryName);
        topic.setSubCategoryName(subCategoryName);
        topic.setDescription(description);
        topic.setSerialNumber(serialNumber);
        topic.setIsAccepted(isAccepted);
        topic.setDiscussion(discussion);
        topic.setNnsMeeting(nnsMeeting);
        topic.setStudent(student);
        topic.setProfessor(professor);
        topic.setProfessors(professors);

        return topicRepository.save(topic);
    }

    @Override
    public Topic remove(Long id) {
        Topic topic = findById(id);
        topicRepository.delete(topic);
        return topic;
    }

    @Override
    public Set<String> getAllSubCategories() {
        return findAll().stream().map(Topic::getSubCategoryName).filter(subCategory -> !subCategory.isEmpty()).collect(Collectors.toSet());
    }
}
