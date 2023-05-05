package mk.ukim.finki.wp.project.ednevnik.repository;

import mk.ukim.finki.wp.project.ednevnik.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {

    Student findByNameAndSurname (String name, String surname);
}
