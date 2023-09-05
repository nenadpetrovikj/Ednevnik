package mk.ukim.finki.wp.project.ednevnik.service.implementation;

import mk.ukim.finki.wp.project.ednevnik.model.Professor;
import mk.ukim.finki.wp.project.ednevnik.model.Student;
import mk.ukim.finki.wp.project.ednevnik.model.Topic;
import mk.ukim.finki.wp.project.ednevnik.model.enumerations.ProfessorRole;
import mk.ukim.finki.wp.project.ednevnik.model.exceptions.StudentFormatException;
import mk.ukim.finki.wp.project.ednevnik.repository.ProfessorRepository;
import mk.ukim.finki.wp.project.ednevnik.service.ProfessorService;
import mk.ukim.finki.wp.project.ednevnik.service.StudentService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
public class ProfessorServiceImplementation implements ProfessorService {

    private final ProfessorRepository professorRepository;

    private final StudentService studentService;

    public ProfessorServiceImplementation(ProfessorRepository professorRepository, StudentService studentService) {
        this.professorRepository = professorRepository;
        this.studentService = studentService;
    }

    @Override
    public List<Professor> findAll() {
        return professorRepository.findAll();
    }

    @Override
    public Professor findById(Long id) {
        return professorRepository.findById(id).orElseGet(() -> null);
    }

    @Override
    public Professor create(String name, String surname, ProfessorRole professorRole) {
        return professorRepository.save(new Professor(name, surname, professorRole));
    }

    @Override
    public Professor update(Long id, String name, String surname, ProfessorRole professorRole) {
        Professor professor = findById(id);

        professor.setName(name);
        professor.setSurname(surname);
        professor.setProfessorRole(professorRole);

        return professorRepository.save(professor);
    }

    @Override
    public Professor remove(Long id) {
        Professor professor = findById(id);

        List<Topic> topics = professor.getTopics();
        for (Topic topic : topics) topic.setProfessor(null);

        List<Topic> topicsMultipleProf = professor.getTopicsWithMultipleProfs();
        for (Topic topic : topicsMultipleProf) topic.getProfessors().remove(professor);

        professorRepository.delete(professor);
        return professor;
    }

    @Override
    public List<Topic> topicsForThisProfSortedByTheirNNSMeetingDate(Professor chosenProf) {
        List<Topic> mergedAllTopics = chosenProf.getTopics();
        List<Long> mergedAllTopicsIds = mergedAllTopics.stream().map(Topic::getId).toList();

        for (Topic topic : chosenProf.getTopicsWithMultipleProfs()) {
            if (!mergedAllTopicsIds.contains(topic.getId()))
                mergedAllTopics.add(topic);
        }

        return mergedAllTopics.stream().sorted(Comparator.<Topic, LocalDate>comparing(topic -> topic.getNnsMeeting().getDate()).reversed()).toList();
    }

    @Override
    public List<Topic> topicsForThisProfessorFilteredBySpecs(Professor chosenProf, String categoryName, String subCategoryName, String studentFullNameId) throws StudentFormatException {
        List<Topic> topicsForThisProfessor = topicsForThisProfSortedByTheirNNSMeetingDate(chosenProf);

        if (!categoryName.equalsIgnoreCase("сите"))
            topicsForThisProfessor = topicsForThisProfessor.stream().filter(topic -> topic.getCategoryName().name().equals(categoryName)).toList();
        if (!subCategoryName.isEmpty())
            topicsForThisProfessor = topicsForThisProfessor.stream().filter(topic -> topic.getSubCategoryName().equals(subCategoryName)).toList();
        if (!studentFullNameId.isEmpty()) {
            Student student = studentService.checkFormatAndReturnStudent(studentFullNameId);
            if (student != null)
                topicsForThisProfessor = topicsForThisProfessor.stream().filter(topic -> Objects.equals(topic.getStudent().getId(), student.getId())).toList();
            else topicsForThisProfessor = List.of();
        }
        return topicsForThisProfessor;
    }
}
