package mk.ukim.finki.wp.project.ednevnik.service.implementation;

import mk.ukim.finki.wp.project.ednevnik.model.Student;
import mk.ukim.finki.wp.project.ednevnik.repository.StudentRepository;
import mk.ukim.finki.wp.project.ednevnik.service.StudentService;
import org.assertj.core.api.Assertions;
import org.checkerframework.checker.nullness.Opt;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

// Example Unit test that verifies the business logic in the service layer
@ExtendWith(MockitoExtension.class)
class StudentServiceImplementationTest {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentServiceImplementation studentService;

    @Test
    void removeStudentInvalidId() {
        Assertions.assertThatThrownBy(() -> studentService.remove(1L)).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void removeStudentValidId() {
        Student student = new Student("John", "Doe", 1L);

        // Mock studentRepository.findById() to return my student
        Mockito.when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        // Call the actual method under test
        Student deletedStudent = studentService.remove(1L);

        // Verify that studentRepository.delete(student) was called in the studentService.remove() method
        Mockito.verify(studentRepository).delete(student);

        // Verify that the studentService.remove() returns the student
        Assertions.assertThat(deletedStudent).isEqualTo(student);
    }

}