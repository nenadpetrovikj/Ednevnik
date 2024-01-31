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

    // Paging test was way too complicated

    @Test
    void filterByProfessor() throws Exception {
        Long professorIdToFilter = 1L;
        Professor professor = new Professor("John", "Doe", ProfessorRole.ПРОФЕСОР);
        professor.setId(professorIdToFilter);
        Mockito.when(professorService.findById(professorIdToFilter)).thenReturn(professor);

        this.mockMvc
                .perform(post("/professors").param("filterByProfessor", professorIdToFilter.toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("master-template"))
                .andExpect(model().attribute("chosenProf", professor))
                .andExpect(model().attribute("title", "Професори"))
                .andExpect(model().attributeExists("professorsInFilter"))
                .andExpect(model().attributeExists("bodyContent"));

        Mockito.verify(professorService, Mockito.times(1)).findById(professorIdToFilter);
    }


    @Test
    void showTopicsForProfessor() throws Exception {
        Long professorIdToShowTopics = 1L;
        Professor professor = new Professor("John", "Doe", ProfessorRole.ПРОФЕСОР);
        professor.setId(professorIdToShowTopics);
        Mockito.when(professorService.findById(professorIdToShowTopics)).thenReturn(professor);

        NNSMeeting nnsMeeting = new NNSMeeting("100", LocalDate.now().minusDays(3));
        NNSMeeting nnsMeeting2 = new NNSMeeting("200", LocalDate.now());
        Student student = new Student("Bob", "Smith", 10L);
        Student student2 = new Student("Tony", "Allen", 20L);
        List<Topic> topicsForProfessor = List.of(new Topic(TopicCategory.ВТОР_ЦИКЛУС, "Subcategory name example", "Description example", "1.1.1", true, "Discussion example", nnsMeeting, student, professor, null),
                                                 new Topic(TopicCategory.ТРЕТ_ЦИКЛУС, "Subcategory name example", "Description example", "1.1.1", true, "Discussion example", nnsMeeting2, student2, professor, null));

        Mockito.when(professorService.topicsForThisProfSortedByTheirNNSMeetingDate(professor)).thenReturn(topicsForProfessor);

        this.mockMvc
                .perform(get("/professors/{id}/topics-list", professorIdToShowTopics))
                .andExpect(status().isOk())
                .andExpect(view().name("master-template")) // Adjust the view name as needed
                .andExpect(model().attribute("chosenProf", professor))
                .andExpect(model().attribute("topics", topicsForProfessor))
                .andExpect(model().attribute("topicCategories", TopicCategory.values()))
                .andExpect(model().attributeExists("subCategories"))
                .andExpect(model().attributeExists("students"))
                .andExpect(model().attribute("title", "Професори"))
                .andExpect(model().attributeExists("bodyContent"));

        Mockito.verify(professorService, Mockito.times(1)).findById(professorIdToShowTopics);
        Mockito.verify(professorService, Mockito.times(1)).topicsForThisProfSortedByTheirNNSMeetingDate(professor);
        Mockito.verify(studentService, Mockito.times(1)).getAllStudentsInFormat();
        Mockito.verify(topicService, Mockito.times(1)).getAllSubCategories();
    }

    @Test
    void showTopicsForProfessorFilteredBySpecs() throws Exception {
        Long professorId = 1L;
        String categoryName = TopicCategory.ВТОР_ЦИКЛУС.name();
        String subCategoryName = "Subcategory name example";
        String studentFullNameId = "Bob Smith 10";

        Professor professor = new Professor("John", "Doe", ProfessorRole.ПРОФЕСОР);
        professor.setId(professorId);
        Mockito.when(professorService.findById(professorId)).thenReturn(professor);

        NNSMeeting nnsMeeting = new NNSMeeting("100", LocalDate.now().minusDays(3));
        Student student = new Student("Bob", "Smith", 10L);
        List<Topic> filteredTopics = List.of(new Topic(TopicCategory.ВТОР_ЦИКЛУС, "Subcategory name example", "Description example", "1.1.1", true, "Discussion example", nnsMeeting, student, professor, null));
        Mockito.when(professorService.topicsForThisProfessorFilteredBySpecs(professor, categoryName, subCategoryName, studentFullNameId)).thenReturn(filteredTopics);

        this.mockMvc
                .perform(post("/professors/{id}/topics-list", professorId)
                        .param("categoryName", categoryName)
                        .param("subCategoryName", subCategoryName)
                        .param("studentFullNameId", studentFullNameId))
                .andExpect(status().isOk())
                .andExpect(view().name("master-template"))
                .andExpect(model().attribute("chosenProf", professor))
                .andExpect(model().attribute("topics", filteredTopics))
                .andExpect(model().attribute("selectedCat", TopicCategory.valueOf(categoryName)))
                .andExpect(model().attribute("selectedSubCat", subCategoryName))
                .andExpect(model().attribute("selectedStudent", studentFullNameId))
                .andExpect(model().attribute("topicCategories", TopicCategory.values()))
                .andExpect(model().attributeExists("subCategories"))
                .andExpect(model().attributeExists("students"))
                .andExpect(model().attribute("title", "Професори"))
                .andExpect(model().attributeExists("bodyContent"));

        Mockito.verify(professorService, Mockito.times(1)).findById(professorId);
        Mockito.verify(professorService, Mockito.times(1)).topicsForThisProfessorFilteredBySpecs(professor, categoryName, subCategoryName, studentFullNameId);
        Mockito.verify(studentService, Mockito.times(1)).getAllStudentsInFormat();
        Mockito.verify(topicService, Mockito.times(1)).getAllSubCategories();
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
        professor.setId(professorIdToUpdate);

        if (professorIdToUpdate != null) {
            Mockito.when(professorService.update(professorIdToUpdate, name, surname, professorRole)).thenReturn(professor);
        } else {
            Mockito.when(professorService.create(name, surname, professorRole)).thenReturn(professor);
        }

        this.mockMvc
                .perform(post("/professors/make-changes")
                        .param("id", String.valueOf(professorIdToUpdate))
                        .param("name", name)
                        .param("surname", surname)
                        .param("professorRole", professorRole.name()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/professors"));

        if (professorIdToUpdate != null) {
            Mockito.verify(professorService, Mockito.times(1)).update(professorIdToUpdate, name, surname, professorRole);
        } else {
            Mockito.verify(professorService, Mockito.times(1)).create(name, surname, professorRole);
        }
    }

    @Test
    void deleteProf() throws Exception {
        Long professorIdToDelete = 1L;

        this.mockMvc
                .perform(get("/professors/{id}/delete", professorIdToDelete))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/professors"));

        Mockito.verify(professorService, Mockito.times(1)).remove(professorIdToDelete);
    }
}