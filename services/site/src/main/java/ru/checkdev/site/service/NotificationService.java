package ru.checkdev.site.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.checkdev.site.dto.SubscribeCategory;
import ru.checkdev.site.dto.SubscribeTopicDTO;
import ru.checkdev.site.dto.UserDTO;
import ru.checkdev.site.dto.UserTopicDTO;

import java.util.List;

@Service
public class NotificationService {

    @Value("${service.notification}/subscribeCategory/")
    private String subscribeCategoryUrl;

    @Value("${service.notification}/subscribeCategory/add")
    private String subscribeCategoryAddUrl;

    @Value("${service.notification}/subscribeCategory/delete")
    private String subscribeCategoryDeleteUrl;

    @Value("${service.notification}/subscribeTopic/")
    private String subscribeTopicUrl;

    @Value("${service.notification}/subscribeTopic/add")
    private String subscribeTopicAddUrl;

    @Value("${service.notification}/subscribeTopic/delete")
    private String subscribeTopicDeleteUrl;

    public void addSubscribeCategory(String token, int userId, int categoryId) throws JsonProcessingException {
        SubscribeCategory subscribeCategory = new SubscribeCategory(userId, categoryId);
        var mapper = new ObjectMapper();
        var out = new RestAuthCall(subscribeCategoryAddUrl).post(
                token, mapper.writeValueAsString(subscribeCategory));
    }

    public void deleteSubscribeCategory(String token, int userId, int categoryId) throws JsonProcessingException {
        SubscribeCategory subscribeCategory = new SubscribeCategory(userId, categoryId);
        var mapper = new ObjectMapper();
        var out = new RestAuthCall(subscribeCategoryDeleteUrl).post(
                token, mapper.writeValueAsString(subscribeCategory));
    }

    public UserDTO findCategoriesByUserId(int id) throws JsonProcessingException {
        var text = new RestAuthCall(subscribeCategoryUrl + id).get();
        var mapper = new ObjectMapper();
        List<Integer> list = mapper.readValue(text, new TypeReference<>() {
        });
        return new UserDTO(id, list);
    }

    public void addSubscribeTopic(String token, int userId, int topicId) throws JsonProcessingException {
        SubscribeTopicDTO subscribeTopicDTO = new SubscribeTopicDTO(userId, topicId);
        var mapper = new ObjectMapper();
        var out = new RestAuthCall(subscribeTopicAddUrl).post(
                token, mapper.writeValueAsString(subscribeTopicDTO));
    }

    public void deleteSubscribeTopic(String token, int userId, int topicId) throws JsonProcessingException {
        SubscribeTopicDTO subscribeTopic = new SubscribeTopicDTO(userId, topicId);
        var mapper = new ObjectMapper();
        var out = new RestAuthCall(subscribeTopicDeleteUrl).post(
                token, mapper.writeValueAsString(subscribeTopic));
    }

    public UserTopicDTO findTopicByUserId(int id) throws JsonProcessingException {
        var text = new RestAuthCall(subscribeTopicUrl + id).get();
        var mapper = new ObjectMapper();
        List<Integer> list = mapper.readValue(text, new TypeReference<>() {
        });
        return new UserTopicDTO(id, list);
    }
}