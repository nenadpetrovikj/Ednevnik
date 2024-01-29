package mk.ukim.finki.wp.project.ednevnik.web;

import mk.ukim.finki.wp.project.ednevnik.model.NNSMeeting;
import mk.ukim.finki.wp.project.ednevnik.model.Professor;
import mk.ukim.finki.wp.project.ednevnik.model.Student;
import mk.ukim.finki.wp.project.ednevnik.model.Topic;
import mk.ukim.finki.wp.project.ednevnik.model.enumerations.ProfessorRole;
import mk.ukim.finki.wp.project.ednevnik.model.enumerations.TopicCategory;
import mk.ukim.finki.wp.project.ednevnik.service.ProfessorService;
import mk.ukim.finki.wp.project.ednevnik.service.StudentService;
import mk.ukim.finki.wp.project.ednevnik.service.TopicService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;


import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
                .andExpect(model().attributeExists("bodyContent"));
    }

    @Test
    void showTopicsForStudent() throws Exception {
        Student student = new Student("John", "Doe", 1L);
        String studentFullNameId = "John Doe 1";
        Mockito.when(studentService.checkFormatAndReturnStudent(studentFullNameId)).thenReturn(student);

        NNSMeeting nnsMeeting = new NNSMeeting("100", LocalDate.now().minusDays(3));
        NNSMeeting nnsMeeting2 = new NNSMeeting("200", LocalDate.now());
        Professor professor = new Professor("Bob", "Smith", ProfessorRole.ПРОФЕСОР);
        Professor professor2 = new Professor("Tony", "Allen", ProfessorRole.АСИСТЕНТ);

        List<Topic> topics = List.of(new Topic(TopicCategory.ВТОР_ЦИКЛУС, "Subcategory name example", "Description example", "1.1.1", true, "Discussion example", nnsMeeting, student, professor, null),
                                     new Topic(TopicCategory.ТРЕТ_ЦИКЛУС, "Subcategory name example", "Description example", "1.1.1", true, "Discussion example", nnsMeeting2, student, professor2, null));
        Mockito.when(studentService.topicsForThisStudentSortedByTheirNNSMeetingDate(student)).thenReturn(topics);

        this.mockMvc
                .perform(post("/students").param("studentFullNameId", studentFullNameId))
                .andExpect(status().isOk())
                .andExpect(view().name("master-template"))
                .andExpect(model().attribute("student", student))
                .andExpect(model().attribute("topics", topics))
                .andExpect(model().attribute("isStudentString", false))
                .andExpect(model().attribute("topicCategories", TopicCategory.values()))
                .andExpect(model().attributeExists("subCategories"))
                .andExpect(model().attributeExists("professors"))
                .andExpect(model().attribute("title", "Студенти"))
                .andExpect(model().attributeExists("bodyContent"));
    }

    @Test
    void showTopicsForStudentFilteredBySpecs() throws Exception {
        Long studentId = 1L;
        String categoryName = TopicCategory.ВТОР_ЦИКЛУС.name();
        String subCategoryName = "Subcategory name example";
        Long professorId = 1L;

        Student student = new Student("John", "Doe", studentId);
        Mockito.when(studentService.findById(studentId)).thenReturn(student);

        NNSMeeting nnsMeeting = new NNSMeeting("100", LocalDate.now().minusDays(3));
        Professor professor = new Professor("Bob", "Smith", ProfessorRole.ПРОФЕСОР);
        professor.setId(professorId);
        List<Topic> topics = List.of(new Topic(TopicCategory.ВТОР_ЦИКЛУС, "Subcategory name example", "Description example", "1.1.1", true, "Discussion example", nnsMeeting, student, professor, null));
        Mockito.when(studentService.topicsForThisStudentFilteredBySpecs(student, categoryName, subCategoryName, professorId)).thenReturn(topics);

        this.mockMvc
                .perform(post("/students/{id}/topics-list", studentId)
                        .param("categoryName", categoryName)
                        .param("subCategoryName", subCategoryName)
                        .param("professorId", professorId.toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("master-template"))
                .andExpect(model().attribute("topics", topics))
                .andExpect(model().attribute("selectedCat", TopicCategory.valueOf(categoryName)))
                .andExpect(model().attribute("selectedSubCat", subCategoryName))
                .andExpect(model().attribute("selectedProf", professorId))
                .andExpect(model().attribute("student", student))
                .andExpect(model().attribute("isStudentString", false))
                .andExpect(model().attribute("topicCategories", TopicCategory.values()))
                .andExpect(model().attributeExists("subCategories"))
                .andExpect(model().attributeExists("professors"))
                .andExpect(model().attribute("title", "Студенти"))
                .andExpect(model().attributeExists("bodyContent"));
    }

    @Test
    void showStudentAdd() throws Exception {
        this.mockMvc
                .perform(get("/students/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("master-template"))
                .andExpect(model().attribute("title", "Додадете Нов Студент"))
                .andExpect(model().attribute("bodyContent", "student-add"));
    }

    @Test
    void showStudentEdit() throws Exception {
        Long studentId = 1L;
        Student student = new Student("John", "Doe", studentId);

        Mockito.when(studentService.findById(studentId)).thenReturn(student);

        this.mockMvc
                .perform(get("/students/{id}/edit", studentId))
                .andExpect(status().isOk())
                .andExpect(view().name("master-template"))
                .andExpect(model().attribute("student", student))
                .andExpect(model().attribute("title", "Сменете Студент"))
                .andExpect(model().attribute("bodyContent", "student-add"));
    }

    @Test
    void studentAdd() throws Exception {
        Long existingStudentId = 1L;
        Long newStudentId = 2L;
        String newName = "New student name example";
        String newSurname = "New student surname example";

        // Simulate updating an existing student
        this.mockMvc
                .perform(post("/students/make-changes")
                        .param("idOld", existingStudentId.toString())
                        .param("idNew", newStudentId.toString())
                        .param("name", newName)
                        .param("surname", newSurname))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/students"));

        // .verify - checks if a specific method on the mocked object was called with the specified arguments
        // .times - specifies the number of times the method should have been called
        Mockito.verify(studentService, Mockito.times(1)).update(existingStudentId, newStudentId, newName, newSurname);
        Mockito.verify(studentService, Mockito.never()).create(Mockito.anyString());

        // Reset the mocks for the next test
        Mockito.reset(studentService);

        // Simulate creating a new student
        Student student = new Student(newName, newSurname, newStudentId);
        Mockito.when(studentService.create(newName + ' ' + newSurname + ' ' + newStudentId)).thenReturn(student);
        this.mockMvc
                .perform(post("/students/make-changes")
                        .param("idNew", newStudentId.toString())
                        .param("name", newName)
                        .param("surname", newSurname))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/students"));

        Mockito.verify(studentService, Mockito.never()).update(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyString(), Mockito.anyString());
        Mockito.verify(studentService, Mockito.times(1)).create(newName + ' ' + newSurname + ' ' + newStudentId);
    }

    @Test
    void studentDelete() throws Exception {
        Long studentIdToDelete = 1L;

        this.mockMvc
                .perform(get("/students/{id}/delete", studentIdToDelete))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/students"));

        Mockito.verify(studentService, Mockito.times(1)).remove(studentIdToDelete);
    }

}