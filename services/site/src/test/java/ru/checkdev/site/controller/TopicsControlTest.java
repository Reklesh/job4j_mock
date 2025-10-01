package ru.checkdev.site.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.checkdev.site.SiteSrv;
import ru.checkdev.site.domain.Breadcrumb;
import ru.checkdev.site.dto.*;
import ru.checkdev.site.service.AuthService;
import ru.checkdev.site.service.CategoriesService;
import ru.checkdev.site.service.NotificationService;
import ru.checkdev.site.service.TopicsService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.IntStream;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

@SpringBootTest(classes = SiteSrv.class)
@AutoConfigureMockMvc
public class TopicsControlTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private TopicsService topicsService;
    @MockBean
    private CategoriesService categoriesService;
    @MockBean
    private AuthService authService;

    @MockBean
    private NotificationService notifications;

    @Test
    public void whenShowTopics() throws Exception {
        var token = "1410";
        var userInfo = new UserInfoDTO();
        var role = new Role();
        role .setId(1);
        role.setValue("ROLE_USER");
        List<TopicDTO> topics = IntStream.range(0, 3).mapToObj(i -> {
            var topic = new TopicDTO();
            topic.setId(i);
            topic.setName(String.format("topic_%d", i));
            topic.setText("Some text");
            topic.setCreated(Calendar.getInstance());
            topic.setUpdated(Calendar.getInstance());
            topic.setCategory(new CategoryDTO(1, String.format("Category_%d", 1)));
            return topic;
        }).toList();
        userInfo.setRoles(List.of(role));
        var userTopicDto = new UserTopicDTO();
        userTopicDto.setId(1);
        userTopicDto.setSubscribeTopicIds(new ArrayList<>());
        when(topicsService.getByCategory(1)).thenReturn(topics);
        when(authService.userInfo(token)).thenReturn(userInfo);
        when(notifications.findTopicByUserId(userInfo.getId())).thenReturn(userTopicDto);
        mockMvc.perform(get("/topics/1").sessionAttr("token", token))
                
                .andExpect(status().isOk())
                .andExpect(view().name("topic/topics"))
                .andExpect(model().attribute("topics", topics))
                .andExpect(model().attribute("userTopicDTO", userTopicDto))
                .andExpect(model().attribute("breadcrumbs", List.of(
                        new Breadcrumb("Главная", "/index"),
                        new Breadcrumb("Категории", "/categories/"),
                        new Breadcrumb("Category_1", "/topics/1"))));
    }
}