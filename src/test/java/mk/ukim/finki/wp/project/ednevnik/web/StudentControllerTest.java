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
    private TopicService topicService;
    @MockBean
    private ProfessorService professorService;

    @Test
    void showStudentList() throws Exception {
        List<String> studentStringFormat = List.of("John Doe 1");
        Mockito.when(studentService.getAllStudentsInFormat()).thenReturn(studentStringFormat);

        this.mockMvc
                .perform(get("/students"))
                .andExpect(status().isOk())
                .andExpect(view().name("master-template"))
                .andExpect(model().attribute("students", studentStringFormat))
                .andExpect(model().attribute("title", "Студенти"))
                .andExpect(model().attribute("bodyContent", "students-page"));

        // .verify - checks if a specific method on the mocked object was called with the specified arguments
        // .times - specifies the number of times the method should have been called
        Mockito.verify(studentService, Mockito.times(1)).getAllStudentsInFormat();
    }

    @Test
    void showTopicsForStudent() throws Exception {
        String studentFullNameId = "John Doe 1";

        Student student = new Student("John", "Doe", 1L);
        Mockito.when(studentService.checkFormatAndReturnStudent(studentFullNameId)).thenReturn(student);

        NNSMeeting nnsMeeting1 = new NNSMeeting("100", LocalDate.now().minusDays(3));
        NNSMeeting nnsMeeting2 = new NNSMeeting("200", LocalDate.now());
        List<Topic> topicsForStudent = List.of(new Topic(TopicCategory.КАДРОВСКИ_ПРАШАЊА, "Subcategory name example", "Description example", "1.1.1", true, "Discussion example", nnsMeeting1, student, null, null),
                                               new Topic(TopicCategory.ОСТАНАТО, "Subcategory name example", "Description example", "1.1.1", true, "Discussion example", nnsMeeting2, student, null, null));
        Mockito.when(studentService.topicsForThisStudentSortedByTheirNNSMeetingDate(student)).thenReturn(topicsForStudent);

        this.mockMvc
                .perform(post("/students").param("studentFullNameId", studentFullNameId))
                .andExpect(status().isOk())
                .andExpect(view().name("master-template"))
                .andExpect(model().attribute("student", student))
                .andExpect(model().attribute("topics", topicsForStudent))
                .andExpect(model().attribute("isStudentString", false))
                .andExpect(model().attribute("topicCategories", TopicCategory.values()))
                .andExpect(model().attributeExists("subCategories"))
                .andExpect(model().attributeExists("professors"))
                .andExpect(model().attribute("title", "Студенти"))
                .andExpect(model().attribute("bodyContent", "students-page"));

        Mockito.verify(studentService, Mockito.times(1)).checkFormatAndReturnStudent(studentFullNameId);
        Mockito.verify(studentService, Mockito.times(1)).topicsForThisStudentSortedByTheirNNSMeetingDate(student);
        Mockito.verify(topicService, Mockito.times(1)).getAllSubCategories();
        Mockito.verify(professorService, Mockito.times(1)).findAll();

        // exception is not handled properly in controller, so method execution will fail before entering if condition
    }

    @Test
    void showTopicsForStudentFilteredBySpecs() throws Exception {
        Long studentId = 1L;
        String categoryName = TopicCategory.КАДРОВСКИ_ПРАШАЊА.name();
        String subCategoryName = "Subcategory name example";
        Long professorId = 1L;

        Student student = new Student("John", "Doe", studentId);
        Mockito.when(studentService.findById(studentId)).thenReturn(student);

        NNSMeeting nnsMeeting = new NNSMeeting("100", LocalDate.now().minusDays(3));
        Professor professor = new Professor("Bob", "Smith", ProfessorRole.ПРОФЕСОР);
        professor.setId(professorId);
        List<Topic> topicsForStudent = List.of(new Topic(TopicCategory.КАДРОВСКИ_ПРАШАЊА, "Subcategory name example", "Description example", "1.1.1", true, "Discussion example", nnsMeeting, student, professor, null));
        Mockito.when(studentService.topicsForThisStudentFilteredBySpecs(student, categoryName, subCategoryName, professorId)).thenReturn(topicsForStudent);

        this.mockMvc
                .perform(post("/students/{id}/topics-list", studentId)
                        .param("categoryName", categoryName)
                        .param("subCategoryName", subCategoryName)
                        .param("professorId", professorId.toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("master-template"))
                .andExpect(model().attribute("topics", topicsForStudent))
                .andExpect(model().attribute("selectedCat", TopicCategory.valueOf(categoryName)))
                .andExpect(model().attribute("selectedSubCat", subCategoryName))
                .andExpect(model().attribute("selectedProf", professorId))
                .andExpect(model().attribute("student", student))
                .andExpect(model().attribute("isStudentString", false))
                .andExpect(model().attribute("topicCategories", TopicCategory.values()))
                .andExpect(model().attributeExists("subCategories"))
                .andExpect(model().attributeExists("professors"))
                .andExpect(model().attribute("title", "Студенти"))
                .andExpect(model().attribute("bodyContent", "students-page"));

        Mockito.verify(studentService, Mockito.times(1)).findById(studentId);
        Mockito.verify(studentService, Mockito.times(1)).topicsForThisStudentFilteredBySpecs(student, categoryName, subCategoryName, professorId);
        Mockito.verify(topicService, Mockito.times(1)).getAllSubCategories();
        Mockito.verify(professorService, Mockito.times(1)).findAll();
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

        Mockito.verify(studentService, Mockito.times(1)).findById(studentId);
    }

    @Test
    void studentAdd() throws Exception {
        Long existingStudentId = 1L;
        Long newStudentId = 2L;
        String newName = "New student name example";
        String newSurname = "New student surname example";

        Student student = new Student(newName, newSurname, newStudentId);

        // Case idOld != null - updating an existing student
        Mockito.when(studentService.update(existingStudentId, newStudentId, newName, newSurname)).thenReturn(student);
        this.mockMvc
                .perform(post("/students/make-changes")
                        .param("idOld", existingStudentId.toString())
                        .param("idNew", newStudentId.toString())
                        .param("name", newName)
                        .param("surname", newSurname))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/students"));

        Mockito.verify(studentService, Mockito.times(1)).update(existingStudentId, newStudentId, newName, newSurname);
        Mockito.verify(studentService, Mockito.never()).create(Mockito.anyString());

        // Reset the mocks for the next test
        Mockito.reset(studentService);

        // Case idOld == null - creating a new student
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

        Student student = new Student("John", "Doe", studentIdToDelete);
        Mockito.when(studentService.remove(studentIdToDelete)).thenReturn(student);

        this.mockMvc
                .perform(get("/students/{id}/delete", studentIdToDelete))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/students"));

        Mockito.verify(studentService, Mockito.times(1)).remove(studentIdToDelete);
    }

}