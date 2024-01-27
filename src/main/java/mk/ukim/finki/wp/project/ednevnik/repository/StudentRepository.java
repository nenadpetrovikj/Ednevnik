package mk.ukim.finki.wp.project.ednevnik.repository;

import mk.ukim.finki.wp.project.ednevnik.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface StudentRepository extends JpaRepository<Student, Long> {

    @Query("Select count(*) from Student")
    int countStudents();
}
