package mk.ukim.finki.wp.project.ednevnik.service.implementation;

import mk.ukim.finki.wp.project.ednevnik.model.Student;
import mk.ukim.finki.wp.project.ednevnik.model.exceptions.NameOrSurnameFieldIsEmptyException;
import mk.ukim.finki.wp.project.ednevnik.repository.StudentRepository;
import mk.ukim.finki.wp.project.ednevnik.service.StudentService;
import org.springframework.stereotype.Service;

import java.util.List;

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

    @Override
    public Student findById(Long id) {
        return studentRepository.findById(id).get();
    }

    @Override
    public Student create(String name, String surname) throws NameOrSurnameFieldIsEmptyException {
        name = name.trim();
        surname = surname.trim();

        if (name.isEmpty() && surname.isEmpty()) return null;

        if (name.isEmpty() || surname.isEmpty()) throw new NameOrSurnameFieldIsEmptyException();

        Student student = studentRepository.findByNameAndSurname(name, surname);
        if (student != null) return student;

        return studentRepository.save(new Student(name, surname));
    }

    @Override
    public Student update(Long id, String name, String surname) {
        Student student = findById(id);

        student.setName(name);
        student.setSurname(surname);

        return studentRepository.save(student);
    }

    @Override
    public Student remove(Long id) {
        Student student = findById(id);
        studentRepository.delete(student);
        return student;
    }
}
