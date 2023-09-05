package mk.ukim.finki.wp.project.ednevnik.service;

import mk.ukim.finki.wp.project.ednevnik.model.Student;
import mk.ukim.finki.wp.project.ednevnik.model.Topic;
import mk.ukim.finki.wp.project.ednevnik.model.exceptions.StudentFormatException;

import java.util.List;

public interface StudentService {
    List<Student> findAll();

    Student findById(Long id);

    Student checkFormatAndReturnStudent(String studentFullNameId) throws StudentFormatException;

    List<String> getAllStudentsInFormat();

    List<Topic> topicsForThisStudentSortedByTheirNNSMeetingDate(Student student);

    List<Topic> topicsForThisStudentFilteredBySpecs(Student student, String categoryName, String subCategoryName, Long professorId);

    Student create(String studentFullNameId) throws StudentFormatException;

    Student update(Long idOld, Long idNew, String name, String surname);

    Student remove(Long id);
}
