package mk.ukim.finki.wp.project.ednevnik.repository;

import mk.ukim.finki.wp.project.ednevnik.model.Student;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
// Anotiran so DataJpa
// Integraciski test (slice test) bidejki se startuva baza vo pozadina i se testira integracijata pomegu Repository layer-ot i bazata
// Se startuva vistinska baza (ne e mokirana), koja po default e h2 (in-memory)
// @DataJpaTest vo sebe ja vklucuva anotacijata @Transactional, sto vo kontekst na testiranje znaci deka
// sekoj test se izvrsuva vo posebna transakcija i po zavrsuvanje na testot se pravi avtomatski rollback
class StudentRepositoryTest {

    @Autowired
    private StudentRepository studentRepository;

    @Test
    void countStudentsShouldReturnZeroWhenNoDataIsPresent() {
        Assertions.assertThat(studentRepository.countStudents()).isEqualTo(0);
    }

    @Test
    void countStudentsNotEmpty() {
        Student student = new Student("John", "Doe", 123L);

        studentRepository.save(student);

        Assertions.assertThat(studentRepository.countStudents()).isEqualTo(1);
    }
}