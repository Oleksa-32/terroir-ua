package com.example.backend.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.backend.dto.wine.WineDto;
import com.example.backend.dto.wine.WineItemDto;
import com.example.backend.utils.TestDataUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WineControllerTest {
    private static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp(@Autowired DataSource ds,
               @Autowired WebApplicationContext ctx) throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
                .apply(springSecurity())
                .build();
        tearDown(ds);
        try (Connection c = ds.getConnection()) {
            c.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    c, new ClassPathResource("database/wine/init-wines.sql"));
        }
    }

    @AfterAll
    static void afterAll(@Autowired DataSource ds) {
        tearDown(ds);
    }

    @SneakyThrows
    private static void tearDown(DataSource ds) {
        try (Connection c = ds.getConnection()) {
            c.setAutoCommit(false);
            ScriptUtils.executeSqlScript(
                    c, new ClassPathResource("database/wine/delete-all-wines.sql"));
        }
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    @DisplayName("POST /wines → 201 Created")
    void createWine_ValidRequest_Success() throws Exception {
        // 1) prepare the JSON part as a MockMultipartFile
        var winePart = new org.springframework.mock.web.MockMultipartFile(
                "wine",
                "wine.json",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(TestDataUtil.createWineRequestDto())
        );
        var imagePart = new org.springframework.mock.web.MockMultipartFile(
                "image",
                "test-image.png",
                MediaType.IMAGE_PNG_VALUE,
                new byte[]{0x12, 0x34}
        );

        MvcResult result = mockMvc.perform(multipart("/wines")
                                .file(winePart)
                                .file(imagePart)
                )
                .andExpect(status().isCreated())
                .andReturn();
        WineDto response = objectMapper.readValue(
                result.getResponse().getContentAsString(), WineDto.class);
        assertThat(response.getId()).isNotNull();
        assertThat(response.getName()).isEqualTo("New Wine");
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "CUSTOMER"})
    @DisplayName("GET /wines → returns all wines")
    void getAllWines_ReturnsAll() throws Exception {
        MvcResult result = mockMvc.perform(get("/wines"))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        JsonNode content = root.get("content");
        WineDto[] wines = objectMapper.treeToValue(content, WineDto[].class);
        assertThat(wines).hasSize(3);
        assertThat(wines[0].getName()).isEqualTo("Wine One");
    }

    @Test
    @WithMockUser
    @DisplayName("GET /wines/1/recommendations → returns recommendations")
    void getRecommendations_ValidId_ReturnsList() throws Exception {
        MvcResult result = mockMvc.perform(get("/wines/1/recommendations"))
                .andExpect(status().isOk())
                .andReturn();
        WineItemDto[] recommendations = objectMapper.readValue(
                result.getResponse().getContentAsString(), WineItemDto[].class);
        assertThat(recommendations).isNotEmpty();
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    @DisplayName("DELETE /wines/1 → 204 No Content")
    void deleteWine_ValidId_NoContent() throws Exception {
        mockMvc.perform(delete("/wines/1"))
                .andExpect(status().isNoContent());
    }
}
