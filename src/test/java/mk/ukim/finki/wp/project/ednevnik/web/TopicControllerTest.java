package mk.ukim.finki.wp.project.ednevnik.web;

import mk.ukim.finki.wp.project.ednevnik.model.NNSMeeting;
import mk.ukim.finki.wp.project.ednevnik.model.Topic;
import mk.ukim.finki.wp.project.ednevnik.model.enumerations.TopicCategory;
import mk.ukim.finki.wp.project.ednevnik.service.TopicService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TopicController.class)
class TopicControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TopicService topicService;

    @Test
    void createTopic() throws Exception {
        Long nnsMeetingId = 1L;
        TopicCategory categoryName = TopicCategory.КАДРОВСКИ_ПРАШАЊА;
        String subCategoryName = "Subcategory name example";
        String serialNumber = "1.1.1";
        Long topicId = 1L;

        NNSMeeting nnsMeeting = new NNSMeeting();
        nnsMeeting.setId(nnsMeetingId);
        Topic topic = new Topic(categoryName, null, null, serialNumber, null, null, nnsMeeting, null, null, null);

        // Case id == null - creating a new topic
        Mockito.when(topicService.create(categoryName, null, null, serialNumber, null, null, nnsMeetingId, null, null, null)).thenReturn(topic);
        this.mockMvc
                .perform(post("/topics")
                        .param("nnsMeetingId", String.valueOf(nnsMeetingId))
                        .param("categoryName", categoryName.toString())
                        .param("serialNumber", serialNumber)
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/nns-meetings/" + nnsMeetingId + "/topics-list"));

        Mockito.verify(topicService, Mockito.times(1)).create(categoryName, null, null, serialNumber, null, null, nnsMeetingId, null, null, null);
        Mockito.verify(topicService, Mockito.never()).update(
                Mockito.anyLong(),
                Mockito.eq(categoryName),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyBoolean(),
                Mockito.anyString(),
                Mockito.anyLong(),
                Mockito.anyString(),
                Mockito.anyLong(),
                Mockito.anyList()
        );

        // Reset the mocks for the next test
        Mockito.reset(topicService);

        // Case id != null - updating an existing topic
        topic.setId(topicId);
        topic.setSubCategoryName("Subcategory name example");
        Mockito.when(topicService.update(topicId, categoryName, subCategoryName, null, serialNumber, null, null, nnsMeetingId, null, null, null)).thenReturn(topic);
        this.mockMvc
                .perform(post("/topics")
                                .param("nnsMeetingId", String.valueOf(nnsMeetingId))
                                .param("categoryName", categoryName.toString())
                                .param("serialNumber", serialNumber)
                                .param("id", String.valueOf(topicId))
                                .param("subCategoryName", subCategoryName)
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/nns-meetings/" + nnsMeetingId + "/topics-list"));

        Mockito.verify(topicService, Mockito.times(1)).update(topicId, categoryName, subCategoryName, null, serialNumber, null, null, nnsMeetingId, null, null, null);
        Mockito.verify(topicService, Mockito.never()).create(
                Mockito.eq(categoryName),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyBoolean(),
                Mockito.anyString(),
                Mockito.anyLong(),
                Mockito.anyString(),
                Mockito.anyLong(),
                Mockito.anyList()
        );
    }

    @Test
    void deleteTopic() throws Exception {
        Long topicId = 1L;

        Long nnsMeetingId = 1L;
        NNSMeeting nnsMeeting = new NNSMeeting();
        nnsMeeting.setId(nnsMeetingId);

        Topic removedTopic = new Topic();
        removedTopic.setId(topicId);
        removedTopic.setNnsMeeting(nnsMeeting);

        // Mock the topicService.remove(id) method to return a Topic instance with NNSMeeting ID
        Mockito.when(topicService.remove(topicId)).thenReturn(removedTopic);

        this.mockMvc
                .perform(get("/topics/{id}/delete", topicId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/nns-meetings/" + nnsMeetingId + "/topics-list"));

        Mockito.verify(topicService, Mockito.times(1)).remove(topicId);
    }
}