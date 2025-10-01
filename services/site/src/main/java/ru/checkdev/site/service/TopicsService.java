package ru.checkdev.site.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.checkdev.site.dto.CategoryDTO;
import ru.checkdev.site.dto.TopicDTO;
import ru.checkdev.site.dto.TopicLiteDTO;
import ru.checkdev.site.dto.TopicIdNameDTO;

import java.util.Calendar;
import java.util.List;

@Service
public class TopicsService {

    @Value("${service.desc}/topics/")
    private String topicsUrl;

    @Value("${service.desc}/topic/")
    private String topicUrl;

    @Value("${service.desc}/topic/name/")
    private String topicNameUrl;

    @Value("${service.desc}/topics/getByCategoryId/")
    private String topicsByCategoryUrl;

    public List<TopicDTO> getByCategory(int id) throws JsonProcessingException {
        var text = new RestAuthCall(topicsUrl + id).get();
        var mapper = new ObjectMapper();
        return mapper.readValue(text, new TypeReference<>() {
        });
    }

    public TopicDTO getById(int id) throws JsonProcessingException {
        var text = new RestAuthCall(topicUrl + id).get();
        var mapper = new ObjectMapper();
        return mapper.readValue(text, new TypeReference<>() {
        });
    }

    public TopicDTO create(String token, TopicLiteDTO topicLite) throws JsonProcessingException {
        var mapper = new ObjectMapper();
        var topic = new TopicDTO();
        topic.setName(topicLite.getName());
        topic.setPosition(topicLite.getPosition());
        topic.setText(topicLite.getText());
        var category = new CategoryDTO();
        category.setId(topicLite.getCategoryId());
        topic.setCategory(category);
        var out = new RestAuthCall(topicUrl).post(token, mapper.writeValueAsString(topic));
        return mapper.readValue(out, TopicDTO.class);
    }

    public void update(String token, TopicDTO topic) throws JsonProcessingException {
        var mapper = new ObjectMapper();
        topic.setUpdated(Calendar.getInstance());
        var json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(topic);
        new RestAuthCall(topicUrl).update(token, json);
    }

    public void delete(String token, int id) throws JsonProcessingException {
        var mapper = new ObjectMapper();
        var topic = new TopicDTO();
        topic.setId(id);
        new RestAuthCall(topicUrl).delete(token, mapper.writeValueAsString(topic));
    }

    public String getNameById(int id) {
        return new RestAuthCall(String.format("%s%d", topicNameUrl, id)).get();
    }

    public List<TopicIdNameDTO> getTopicIdNameDtoByCategory(int categoryId)
            throws JsonProcessingException {
        var text = new RestAuthCall(String.format("%s%d", topicsByCategoryUrl, categoryId)).get();
        var mapper = new ObjectMapper();
        return mapper.readValue(text, new TypeReference<>() {
        });
    }
}
