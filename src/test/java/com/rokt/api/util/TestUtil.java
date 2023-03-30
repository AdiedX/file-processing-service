package com.rokt.api.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rokt.api.model.FileEntry;
import com.rokt.api.model.FileProcessingRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class TestUtil {
    public final String TESTING_PROPERTY = "testing";
    public final String TESTING_PROPERTY_VALUE = "true";
    public final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    public ResultActions performHTTPPost(MockMvc mvc, FileProcessingRequest request) throws Exception {
        return mvc.perform(
            post("/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request))
        );
    }

    public void assertValidResponse(ResultActions resultActions, List<FileEntry> expectedResult) throws Exception {
        MvcResult result = resultActions
            .andExpect(status().isOk())
            .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
            .andReturn();

        assertThat(result.getResponse().getContentAsString())
            .isEqualTo(objectMapper.writeValueAsString(expectedResult));
    }

    public void assertInvalidResponse(ResultActions resultActions) throws Exception {
        resultActions
            .andExpect(status().isOk())
            .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(content().json("[]"));
    }

    public String asJsonString(final Object obj) throws JsonProcessingException {
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return objectMapper.writeValueAsString(obj);
    }

    public List<FileEntry> buildFileEntries(List<String> textData) {
        return textData.stream().map(line -> {
            String[] parts = line.split(" ");
            return new FileEntry(Instant.parse(parts[0]), parts[1], parts[2]);
        }).toList();
    }
}
