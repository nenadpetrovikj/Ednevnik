package mk.ukim.finki.wp.project.ednevnik.web;

import mk.ukim.finki.wp.project.ednevnik.model.Student;
import mk.ukim.finki.wp.project.ednevnik.service.ProfessorService;
import mk.ukim.finki.wp.project.ednevnik.service.StudentService;
import mk.ukim.finki.wp.project.ednevnik.service.TopicService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;


import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentService studentService;

    @MockBean
    private ProfessorService professorService;

    @MockBean
    private TopicService topicService;


    @Test
    void showStudentList() throws Exception {
        Student student = new Student("John", "Doe", 1L);

        Mockito.when(studentService.findAll()).thenReturn(List.of(student));

        List<String> expectedResult = List.of("John Doe 1");

//        Dokolku vo kontrolerot se povikuvase direkt findAllAsString();
//        List<String> expectedResult = List.of("John Doe 1");
//        Mockito.when(studentService.findAllAsString()).thenReturn(expectedResult);

        this.mockMvc
                .perform(get("/students"))
                .andExpect(status().isOk())
                .andExpect(view().name("master-template"))
                .andExpect(model().attribute("students", expectedResult))
                .andExpect(model().attribute("title", "Студенти"))
                .andExpect(model().attributeExists("bodyContent"))
        ;
    }
}