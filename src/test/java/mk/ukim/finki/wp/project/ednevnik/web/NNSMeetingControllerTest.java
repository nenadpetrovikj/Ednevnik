package mk.ukim.finki.wp.project.ednevnik.web;

import mk.ukim.finki.wp.project.ednevnik.model.NNSMeeting;
import mk.ukim.finki.wp.project.ednevnik.model.Student;
import mk.ukim.finki.wp.project.ednevnik.model.Topic;
import mk.ukim.finki.wp.project.ednevnik.model.enumerations.TopicCategory;
import mk.ukim.finki.wp.project.ednevnik.service.NNSMeetingService;
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

@WebMvcTest(NNSMeetingController.class)
class NNSMeetingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NNSMeetingService nnsMeetingService;
    @MockBean
    private ProfessorService professorService;
    @MockBean
    private StudentService studentService;
    @MockBean
    private TopicService topicService;

    // Paging test to be done some other time, because of time constraint

    @Test
    void findAllHeldBeforeSelectedDateDesc() throws Exception {
        LocalDate selectedDate = LocalDate.of(2023, 1, 1);

        List<NNSMeeting> filteredNNSMeetingsDesc = List.of(new NNSMeeting("200", LocalDate.of(2023, 1, 1)),
                                                           new NNSMeeting("100", LocalDate.of(2022, 1, 1)));
        Mockito.when(nnsMeetingService.findAllHeldBeforeSelectedDateDesc(selectedDate)).thenReturn(filteredNNSMeetingsDesc);

        Mockito.when(nnsMeetingService.findAllSortedByDateDesc()).thenReturn(filteredNNSMeetingsDesc);
        // returns the same result since .findAllHeldBeforeSelectedDateDesc already does the sorting by date internally

        this.mockMvc
                .perform(post("/nns-meetings")
                        .param("date", selectedDate.toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("master-template"))
                .andExpect(model().attribute("latest", filteredNNSMeetingsDesc.get(0)))
                .andExpect(model().attribute("nnsMeetings", filteredNNSMeetingsDesc))
                .andExpect(model().attribute("selectedDate", selectedDate))
                .andExpect(model().attribute("title", "ННС Седници"))
                .andExpect(model().attribute("bodyContent", "nns-meetings-page"));

        Mockito.verify(nnsMeetingService, Mockito.times(1)).findAllHeldBeforeSelectedDateDesc(selectedDate);
        Mockito.verify(nnsMeetingService, Mockito.times(2)).findAllSortedByDateDesc();
    }

    @Test
    void getNNSMeetingAddPage() throws Exception {
        this.mockMvc
                .perform(get("/nns-meetings/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("master-template"))
                .andExpect(model().attribute("title", "Додади Нова Седница"))
                .andExpect(model().attribute("bodyContent", "nns-meeting-add"));
    }

    @Test
    void createNNSMeeting() throws Exception {
        String serialCode = "200";
        LocalDate date = LocalDate.of(2023, 1, 1);

        NNSMeeting nnsMeeting = new NNSMeeting(serialCode, date);
        Mockito.when(nnsMeetingService.create(serialCode, date)).thenReturn(nnsMeeting);

        this.mockMvc
                .perform(post("/nns-meetings/add")
                        .param("serialCode", serialCode)
                        .param("date", date.toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/nns-meetings"));

        Mockito.verify(nnsMeetingService, Mockito.times(1)).create(serialCode, date);
    }

    @Test
    void showAddTopicsPage() throws Exception {
        Long meetingId = 1L;

        NNSMeeting nnsMeeting = new NNSMeeting("200", LocalDate.of(2023, 1, 1));
        nnsMeeting.setId(meetingId);
        Mockito.when(nnsMeetingService.findById(meetingId)).thenReturn(nnsMeeting);

        mockMvc.perform(get("/nns-meetings/{id}/add-topic", meetingId))
                .andExpect(status().isOk())
                .andExpect(view().name("master-template"))
                .andExpect(model().attribute("nnsMeeting", nnsMeeting))
                .andExpect(model().attribute("student", ""))
                .andExpect(model().attribute("topicCategories", TopicCategory.values()))
                .andExpect(model().attributeExists("professors"))
                .andExpect(model().attributeExists("students"))
                .andExpect(model().attributeExists("subCategories"))
                .andExpect(model().attribute("title", "Додадете Нов Запис"))
                .andExpect(model().attribute("bodyContent", "add-topic-page"));

        Mockito.verify(nnsMeetingService, Mockito.times(1)).findById(meetingId);
        Mockito.verify(professorService, Mockito.times(1)).findAll();
        Mockito.verify(studentService, Mockito.times(1)).getAllStudentsInFormat();
        Mockito.verify(topicService, Mockito.times(1)).getAllSubCategories();
    }

    @Test
    void getEditTopicPage() throws Exception {
        Long meetingId = 1L;
        Long topicId = 1L;

        NNSMeeting nnsMeeting = new NNSMeeting("200", LocalDate.of(2023, 1, 1));
        nnsMeeting.setId(meetingId);
        Mockito.when(nnsMeetingService.findById(meetingId)).thenReturn(nnsMeeting);

        Student student = new Student("Bob", "Smith", 1L);
        Topic topic = new Topic(TopicCategory.ОСТАНАТО, "Subcategory name example", "Description example", "1.1.1", true, "Discussion example", nnsMeeting, student, null, null);
        topic.setId(topicId);
        Mockito.when(topicService.findById(topicId)).thenReturn(topic);

        String expectedStudentString = "Bob Smith 1";

        this.mockMvc
                .perform(get("/nns-meetings/{id}/edit-topic/{topicId}", meetingId, topicId))
                .andExpect(status().isOk())
                .andExpect(view().name("master-template"))
                .andExpect(model().attribute("nnsMeeting", nnsMeeting))
                .andExpect(model().attribute("student", expectedStudentString))
                .andExpect(model().attribute("topic", topic))
                .andExpect(model().attribute("topicCategories", TopicCategory.values()))
                .andExpect(model().attributeExists("professors"))
                .andExpect(model().attributeExists("students"))
                .andExpect(model().attributeExists("subCategories"))
                .andExpect(model().attribute("title", "Сменете Запис"))
                .andExpect(model().attribute("bodyContent", "add-topic-page"));

        Mockito.verify(nnsMeetingService, Mockito.times(1)).findById(meetingId);
        Mockito.verify(professorService, Mockito.times(1)).findAll();
        Mockito.verify(studentService, Mockito.times(1)).getAllStudentsInFormat();
        Mockito.verify(topicService, Mockito.times(1)).getAllSubCategories();
        Mockito.verify(topicService, Mockito.times(1)).findById(topicId);

        // Case topic.getStudent() == null
        topic.setStudent(null);
        expectedStudentString = "";
        this.mockMvc
                .perform(get("/nns-meetings/{id}/edit-topic/{topicId}", meetingId, topicId))
                .andExpect(status().isOk())
                .andExpect(view().name("master-template"))
                .andExpect(model().attribute("student", expectedStudentString))
                .andExpect(model().attribute("title", "Сменете Запис"))
                .andExpect(model().attribute("bodyContent", "add-topic-page"));
    }

    @Test
    void showListTopicsPage() throws Exception {
        Long meetingId = 1L;

        NNSMeeting nnsMeeting = new NNSMeeting("200", LocalDate.of(2023, 1, 1));
        nnsMeeting.setId(meetingId);
        Mockito.when(nnsMeetingService.findById(meetingId)).thenReturn(nnsMeeting);

        List<Topic> topicsForNNSMeeting = List.of(new Topic(TopicCategory.ОСТАНАТО, "Subcategory name example", "Description example", "1.1.1", true, "Discussion example", nnsMeeting, null, null, null),
                                                  new Topic(TopicCategory.ОСТАНАТО, "Subcategory name example", "Description example", "2.1.1", true, "Discussion example", nnsMeeting, null, null, null));
        Mockito.when(nnsMeetingService.sortTopicsBySerialNumberForNNSMeeting(meetingId)).thenReturn(topicsForNNSMeeting);

        mockMvc.perform(get("/nns-meetings/{id}/topics-list", meetingId))
                .andExpect(status().isOk())
                .andExpect(view().name("master-template"))
                .andExpect(model().attribute("nnsMeeting", nnsMeeting))
                .andExpect(model().attribute("topics", topicsForNNSMeeting))
                .andExpect(model().attribute("title", "Преглед На Седница"))
                .andExpect(model().attribute("bodyContent", "list-topics"));

        Mockito.verify(nnsMeetingService, Mockito.times(1)).findById(meetingId);
        Mockito.verify(nnsMeetingService, Mockito.times(1)).sortTopicsBySerialNumberForNNSMeeting(meetingId);
    }
}
