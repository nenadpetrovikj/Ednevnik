package mk.ukim.finki.wp.project.ednevnik.service.implementation;

import mk.ukim.finki.wp.project.ednevnik.model.Professor;
import mk.ukim.finki.wp.project.ednevnik.model.Student;
import mk.ukim.finki.wp.project.ednevnik.model.Topic;
import mk.ukim.finki.wp.project.ednevnik.model.exceptions.StudentFormatException;
import mk.ukim.finki.wp.project.ednevnik.repository.StudentRepository;
import mk.ukim.finki.wp.project.ednevnik.service.StudentService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
public class StudentServiceImplementation implements StudentService {

    private final StudentRepository studentRepository;

    public StudentServiceImplementation(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    public List<Student> findAll() {
        return studentRepository.findAll();
    }
//    public List<String> findAllAsString() {
//        return studentRepository.findAll().stream().map(student -> student.getName() + ' ' + student.getSurname() + ' ' + student.getId()).toList();
//    }

    @Override
    public Student findById(Long id) {
        return studentRepository.findById(id).orElseThrow();
    }

    @Override
    public List<String> getAllStudentsInFormat() {
        return findAll().stream().map(student -> student.getName() + ' ' + student.getSurname() + ' ' + student.getId()).toList();
    }

    @Override
    public Student checkFormatAndReturnStudent(String studentFullNameId) throws StudentFormatException {
        List<String> split = Arrays.stream(studentFullNameId.trim().split(" ")).map(String::trim).toList();
        if (split.size() != 3) throw new StudentFormatException();
        Student student = studentRepository.findById(Long.parseLong(split.get(2))).orElse(null);

        // user can enter name surname and write the wrong id. If that's the case then some kind of an exception must be thrown
        if (student != null && student.getName().equals(split.get(0)) && student.getSurname().equals(split.get(1)))
            return student;
        return null;
    }

    @Override
    public List<Topic> topicsForThisStudentSortedByTheirNNSMeetingDate(Student student) {
        return student.getTopics().stream()
                .sorted(Comparator.<Topic, LocalDate>comparing(topic -> topic.getNnsMeeting().getDate()).reversed()).toList();
    }

    @Override
    public List<Topic> topicsForThisStudentFilteredBySpecs(Student student, String categoryName, String subCategoryName, Long professorId) {
        List<Topic> topics = topicsForThisStudentSortedByTheirNNSMeetingDate(student);

        if (!categoryName.equalsIgnoreCase("сите"))
            topics = topics.stream().filter(topic -> topic.getCategoryName().name().equals(categoryName)).toList();
        if (!subCategoryName.isEmpty())
            topics = topics.stream().filter(topic -> topic.getSubCategoryName().equals(subCategoryName)).toList();
        if (professorId != -1)
            topics = topics.stream().filter(topic -> {
                if (Objects.equals(topic.getProfessor().getId(), professorId)) return true;
                return topic.getProfessors().stream().map(Professor::getId).toList().contains(professorId);
            }).toList();
        return topics;
    }

    @Override
    public Student create(String studentFullNameId) throws StudentFormatException {
        if (studentFullNameId.isEmpty()) return null;

        List<String> split = Arrays.stream(studentFullNameId.trim().split(" ")).map(String::trim).toList();

        Student student = checkFormatAndReturnStudent(studentFullNameId);
        if (student != null) return student;

        return studentRepository.save(new Student(split.get(0), split.get(1), Long.parseLong(split.get(2))));
    }

    @Override
    public Student update(Long idOld, Long idNew, String name, String surname) {
        Student student = findById(idOld);

        if (findById(idNew) == null) {
            Student newStudent = new Student(name, surname, idNew);
            remove(idOld);
            return studentRepository.save(newStudent);
        }

        student.setName(name);
        student.setSurname(surname);

        return studentRepository.save(student);
    }

    @Override
    public Student remove(Long id) {
        Student student = findById(id);
        List<Topic> topics = student.getTopics();
        for (Topic topic : topics) topic.setStudent(null);

        studentRepository.delete(student);
        return student;
    }
}
