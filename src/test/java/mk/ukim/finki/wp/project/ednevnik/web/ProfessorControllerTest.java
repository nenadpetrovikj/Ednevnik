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

@WebMvcTest(ProfessorController.class)
class ProfessorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProfessorService professorService;
    @MockBean
    private TopicService topicService;
    @MockBean
    private StudentService studentService;

    // Paging test to be done some other time, because of time constraint

    @Test
    void filterByProfessor() throws Exception {
        Long professorId = 1L;

        Professor professor = new Professor("John", "Doe", ProfessorRole.ПРОФЕСОР);
        professor.setId(professorId);
        Mockito.when(professorService.findById(professorId)).thenReturn(professor);

        this.mockMvc
                .perform(post("/professors").param("filterByProfessor", professorId.toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("master-template"))
                .andExpect(model().attribute("chosenProf", professor))
                .andExpect(model().attribute("showTopics", false))
                .andExpect(model().attributeExists("professorsInFilter"))
                .andExpect(model().attribute("title", "Професори"))
                .andExpect(model().attribute("bodyContent", "professors-page"));

        Mockito.verify(professorService, Mockito.times(1)).findById(professorId);
        Mockito.verify(professorService, Mockito.times(2)).findAll();

        // professorService.findAllWithPagination(pageable); - didn't focus on paging related tests
    }


    @Test
    void showTopicsForProfessor() throws Exception {
        Long professorId = 1L;

        Professor professor = new Professor("John", "Doe", ProfessorRole.ПРОФЕСОР);
        professor.setId(professorId);
        Mockito.when(professorService.findById(professorId)).thenReturn(professor);

        NNSMeeting nnsMeeting = new NNSMeeting("100", LocalDate.now().minusDays(3));
        NNSMeeting nnsMeeting2 = new NNSMeeting("200", LocalDate.now());
        List<Topic> topicsForProfessor = List.of(new Topic(TopicCategory.КАДРОВСКИ_ПРАШАЊА, "Subcategory name example", "Description example", "1.1.1", true, "Discussion example", nnsMeeting, null, professor, null),
                                                 new Topic(TopicCategory.ОСТАНАТО, "Subcategory name example", "Description example", "1.1.1", true, "Discussion example", nnsMeeting2, null, professor, null));
        Mockito.when(professorService.topicsForThisProfSortedByTheirNNSMeetingDate(professor)).thenReturn(topicsForProfessor);

        this.mockMvc
                .perform(get("/professors/{id}/topics-list", professorId))
                .andExpect(status().isOk())
                .andExpect(view().name("master-template"))
                .andExpect(model().attribute("chosenProf", professor))
                .andExpect(model().attribute("topics", topicsForProfessor))
                .andExpect(model().attribute("topicCategories", TopicCategory.values()))
                .andExpect(model().attribute("showTopics", true))
                .andExpect(model().attributeExists("professorsInFilter"))
                .andExpect(model().attributeExists("subCategories"))
                .andExpect(model().attributeExists("students"))
                .andExpect(model().attribute("title", "Професори"))
                .andExpect(model().attribute("bodyContent", "professors-page"));

        Mockito.verify(professorService, Mockito.times(1)).findById(professorId);
        Mockito.verify(professorService, Mockito.times(1)).topicsForThisProfSortedByTheirNNSMeetingDate(professor);
        Mockito.verify(professorService, Mockito.times(2)).findAll();
        Mockito.verify(studentService, Mockito.times(1)).getAllStudentsInFormat();
        Mockito.verify(topicService, Mockito.times(1)).getAllSubCategories();
    }

    @Test
    void showTopicsForProfessorFilteredBySpecs() throws Exception {
        Long professorId = 1L;
        String categoryName = TopicCategory.КАДРОВСКИ_ПРАШАЊА.name();
        String subCategoryName = "Subcategory name example";
        String studentFullNameId = "Bob Smith 1";

        Professor professor = new Professor("John", "Doe", ProfessorRole.ПРОФЕСОР);
        professor.setId(professorId);
        Mockito.when(professorService.findById(professorId)).thenReturn(professor);

        NNSMeeting nnsMeeting = new NNSMeeting("100", LocalDate.now().minusDays(3));
        Student student = new Student("Bob", "Smith", 1L);
        List<Topic> topicsForProfessor = List.of(new Topic(TopicCategory.КАДРОВСКИ_ПРАШАЊА, "Subcategory name example", "Description example", "1.1.1", true, "Discussion example", nnsMeeting, student, professor, null));
        Mockito.when(professorService.topicsForThisProfessorFilteredBySpecs(professor, categoryName, subCategoryName, studentFullNameId)).thenReturn(topicsForProfessor);

        this.mockMvc
                .perform(post("/professors/{id}/topics-list", professorId)
                        .param("categoryName", categoryName)
                        .param("subCategoryName", subCategoryName)
                        .param("studentFullNameId", studentFullNameId))
                .andExpect(status().isOk())
                .andExpect(view().name("master-template"))
                .andExpect(model().attribute("chosenProf", professor))
                .andExpect(model().attribute("topics", topicsForProfessor))
                .andExpect(model().attribute("selectedCat", TopicCategory.valueOf(categoryName)))
                .andExpect(model().attribute("selectedSubCat", subCategoryName))
                .andExpect(model().attribute("selectedStudent", studentFullNameId))
                .andExpect(model().attribute("topicCategories", TopicCategory.values()))
                .andExpect(model().attribute("showTopics", true))
                .andExpect(model().attributeExists("professorsInFilter"))
                .andExpect(model().attributeExists("subCategories"))
                .andExpect(model().attributeExists("students"))
                .andExpect(model().attribute("title", "Професори"))
                .andExpect(model().attribute("bodyContent", "professors-page"));

        Mockito.verify(professorService, Mockito.times(1)).findById(professorId);
        Mockito.verify(professorService, Mockito.times(1)).topicsForThisProfessorFilteredBySpecs(professor, categoryName, subCategoryName, studentFullNameId);
        Mockito.verify(professorService, Mockito.times(2)).findAll();
        Mockito.verify(studentService, Mockito.times(1)).getAllStudentsInFormat();
        Mockito.verify(topicService, Mockito.times(1)).getAllSubCategories();

        // Case categoryName == "Сите"
        this.mockMvc
                .perform(post("/professors/{id}/topics-list", professorId)
                        .param("categoryName", "Сите")
                        .param("subCategoryName", subCategoryName)
                        .param("professorId", professorId.toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("master-template"))
                .andExpect(model().attributeDoesNotExist("selectedCat"))
                .andExpect(model().attribute("title", "Професори"))
                .andExpect(model().attribute("bodyContent", "professors-page"));
    }

    @Test
    void showProfessorAdd() throws Exception {
        this.mockMvc
                .perform(get("/professors/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("master-template"))
                .andExpect(model().attribute("professorRoles", ProfessorRole.values()))
                .andExpect(model().attribute("title", "Додадете Нов Професор"))
                .andExpect(model().attribute("bodyContent", "professor-add"));
    }


    @Test
    void showProfessorEdit() throws Exception {
        Long professorId = 1L;

        Professor professor = new Professor("John", "Doe", ProfessorRole.ПРОФЕСОР);
        professor.setId(professorId);
        Mockito.when(professorService.findById(professorId)).thenReturn(professor);

        this.mockMvc
                .perform(get("/professors/{id}/edit", professorId))
                .andExpect(status().isOk())
                .andExpect(view().name("master-template"))
                .andExpect(model().attribute("professor", professor))
                .andExpect(model().attribute("professorRoles", ProfessorRole.values()))
                .andExpect(model().attribute("title", "Сменете Професор"))
                .andExpect(model().attribute("bodyContent", "professor-add"));

        Mockito.verify(professorService, Mockito.times(1)).findById(professorId);
    }

    @Test
    void professorAdd() throws Exception {
        Long professorIdToUpdate = 1L;
        String name = "John";
        String surname = "Doe";
        ProfessorRole professorRole = ProfessorRole.ПРОФЕСОР;

        Professor professor = new Professor(name, surname, professorRole);

        // Case id == null - creating a new professor
        Mockito.when(professorService.create(name, surname, professorRole)).thenReturn(professor);
        this.mockMvc
                .perform(post("/professors/make-changes")
                        .param("name", name)
                        .param("surname", surname)
                        .param("professorRole", professorRole.name()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/professors"));
        Mockito.verify(professorService, Mockito.never()).update(Mockito.anyLong(), Mockito.anyString(), Mockito.anyString(), Mockito.eq(professorRole));
        Mockito.verify(professorService, Mockito.times(1)).create(name, surname, professorRole);

        // Reset the mocks for the next test
        Mockito.reset(professorService);

        // Case id != null - updating an existent professor
        professor.setId(professorIdToUpdate);
        Mockito.when(professorService.update(professorIdToUpdate, name, surname, professorRole)).thenReturn(professor);
        this.mockMvc
                .perform(post("/professors/make-changes")
                        .param("id", String.valueOf(professorIdToUpdate))
                        .param("name", name)
                        .param("surname", surname)
                        .param("professorRole", professorRole.name()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/professors"));
        Mockito.verify(professorService, Mockito.times(1)).update(professorIdToUpdate, name, surname, professorRole);
        Mockito.verify(professorService, Mockito.never()).create(Mockito.anyString(), Mockito.anyString(), Mockito.any());
    }

    @Test
    void deleteProf() throws Exception {
        Long professorIdToDelete = 1L;

        Professor professor = new Professor("John", "Doe", ProfessorRole.ПРОФЕСОР);
        professor.setId(professorIdToDelete);
        Mockito.when(professorService.remove(professorIdToDelete)).thenReturn(professor);

        this.mockMvc
                .perform(get("/professors/{id}/delete", professorIdToDelete))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/professors"));

        Mockito.verify(professorService, Mockito.times(1)).remove(professorIdToDelete);
    }
}