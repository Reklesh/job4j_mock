package ru.checkdev.site.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.checkdev.site.dto.FilterDTO;

@Service
public class FilterService {

    @Value("${service.mock}/filter/")
    private String filterUrl;

    @Value("${service.mock}/filter/delete/")
    private String filterDeleteUrl;

    public FilterDTO save(String token, FilterDTO filter) throws JsonProcessingException {
        var mapper = new ObjectMapper();
        var out = new RestAuthCall(filterUrl).post(token, mapper.writeValueAsString(filter));
        return mapper.readValue(out, FilterDTO.class);
    }

    public FilterDTO getByUserId(String token, int userId) throws JsonProcessingException {
        var text = new RestAuthCall(String.format("%s%d", filterUrl, userId)).get(token);
        return new ObjectMapper().readValue(text, new TypeReference<>() {
        });
    }

    public void deleteByUserId(String token, int userId) throws JsonProcessingException {
        var mapper = new ObjectMapper();
        new RestAuthCall(String.format("%s%d", filterDeleteUrl, userId)).delete(
                token,
                mapper.writeValueAsString(userId)
        );
    }
}
