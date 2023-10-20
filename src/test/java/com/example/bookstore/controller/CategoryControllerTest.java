package com.example.bookstore.controller;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.bookstore.dto.book.BookDtoWithoutCategoryIds;
import com.example.bookstore.dto.category.CategoryDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CategoryControllerTest {
    protected static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(
            @Autowired DataSource dataSource,
            @Autowired WebApplicationContext applicationContext
    ) throws SQLException {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
        teardown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/categories/add-categories.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/books/add-books.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/categories/associate-categories-to-books.sql"));
        }
    }

    private CategoryDto createCategoryDto(String name, String description) {
        return new CategoryDto().setName(name).setDescription(description);
    }

    private String convertDtoToJson(Object dto) throws JsonProcessingException {
        return objectMapper.writeValueAsString(dto);
    }

    @AfterAll
    static void afterAll(
            @Autowired DataSource dataSource
    ) {
        teardown(dataSource);
    }

    @SneakyThrows
    private static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/categories/remove-categories-from-books.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/categories/remove-categories.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/books/remove-books.sql"));
        }
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @Sql(scripts = "classpath:database/categories/remove-classics-category.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Create a new category")
    void createCategory_ValidRequestDto_Success() throws Exception {
        CategoryDto requestDto = createCategoryDto("Classics", "Classics");

        CategoryDto expected = new CategoryDto()
                .setName(requestDto.getName())
                .setDescription(requestDto.getDescription());

        String jsonRequest = convertDtoToJson(requestDto);

        MvcResult result = mockMvc.perform(
                        post("/api/categories")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        CategoryDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                CategoryDto.class);

        assertNotNull(actual);
        assertNotNull(actual.getId());
        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Create a new category with not valid request body")
    void createCategory_NotValidRequestDto_BadRequest() throws Exception {
        CategoryDto requestDto = createCategoryDto(null, "Fantasy");

        String jsonRequest = convertDtoToJson(requestDto);

        MvcResult result = mockMvc.perform(
                        post("/api/categories")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @WithMockUser
    @Test
    @DisplayName("Get all categories")
    void getAll_Success() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andReturn();

        List<CategoryDto> response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<>() {
                });

        assertThat(response).hasSize(3);
    }

    @WithMockUser
    @Test
    @DisplayName("Get category by id")
    void getCategoryById_ValidId_ReturnsListOfCategoryDto() throws Exception {
        long id = 1L;
        CategoryDto expected = new CategoryDto()
                .setId(1L)
                .setName("Fantasy")
                .setDescription("Fantasy");

        MvcResult result = mockMvc.perform(get("/api/categories/" + id))
                .andExpect(status().isOk())
                .andReturn();

        CategoryDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                CategoryDto.class);

        assertEquals(expected, actual);
    }

    @WithMockUser
    @Test
    @DisplayName("Get category by non existing id")
    void getCategoryById_NotValidId_NotFound() throws Exception {
        long nonExistingId = 100L;

        mockMvc.perform(get("/api/categories/" + nonExistingId))
                .andExpect(status().isNotFound());
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Update category")
    void updateCategory_ValidIdValidRequestDto_Success() throws Exception {
        Long id = 1L;
        CategoryDto requestDto = createCategoryDto("Updated Fantasy", "Updated Fantasy");

        String jsonRequest = convertDtoToJson(requestDto);

        CategoryDto expected = new CategoryDto()
                .setId(id)
                .setName(requestDto.getName())
                .setDescription(requestDto.getDescription());

        MvcResult result = mockMvc.perform(put("/api/categories/" + id)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        CategoryDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                CategoryDto.class);

        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertEquals(expected, actual);
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Update category with not valid id")
    void updateCategory_NotValidId_NotFound() throws Exception {
        long nonExistingId = 100L;
        CategoryDto requestDto = createCategoryDto("Updated Fantasy", "Updated Fantasy");

        String jsonRequest = convertDtoToJson(requestDto);

        mockMvc.perform(put("/api/categories/" + nonExistingId)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Update category with not valid request body")
    void updateCategory_NotValidRequestDto_BadRequest() throws Exception {
        long id = 1L;
        CategoryDto requestDto = createCategoryDto(null, "Updated Fantasy");

        String jsonRequest = convertDtoToJson(requestDto);

        mockMvc.perform(put("/api/categories/" + id)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @Sql(scripts = "classpath:database/categories/add-classics-category.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @DisplayName("Delete category by id")
    void deleteCategory_ValidId_Success() throws Exception {
        long id = 4L;

        MvcResult resultBeforeDelete = mockMvc.perform(
                        get("/api/categories")
                                .with(user("user")))
                .andExpect(status().isOk())
                .andReturn();

        List<CategoryDto> categoriesBeforeDelete = objectMapper.readValue(
                resultBeforeDelete.getResponse().getContentAsString(),
                new TypeReference<>() {
                });

        mockMvc.perform(delete("/api/categories/" + id))
                .andExpect(status().isOk());

        MvcResult resultAfterDelete = mockMvc.perform(
                        get("/api/categories")
                                .with(user("user")))
                .andExpect(status().isOk())
                .andReturn();

        List<CategoryDto> categoriesAfterDelete = objectMapper.readValue(
                resultAfterDelete.getResponse().getContentAsString(),
                new TypeReference<>() {
                });

        assertThat(categoriesBeforeDelete).hasSize(4);
        assertThat(categoriesAfterDelete).hasSize(3);
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Delete category by non existing id")
    void deleteCategory_NotValidId() throws Exception {
        long nonExistingId = 100L;

        mockMvc.perform(delete("/api/categories/" + nonExistingId))
                .andExpect(status().isOk());

        MvcResult result = mockMvc.perform(
                        get("/api/categories")
                                .with(user("user")))
                .andExpect(status().isOk())
                .andReturn();

        List<CategoryDto> response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<>() {
                });

        assertThat(response).hasSize(3);
    }

    @WithMockUser
    @Test
    @DisplayName("Get books by category id")
    void getBooksByCategoryId_ValidId_ReturnsListOfBooks() throws Exception {
        long id = 1L;

        MvcResult result = mockMvc.perform(get("/api/categories/" + id + "/books"))
                .andExpect(status().isOk())
                .andReturn();

        List<BookDtoWithoutCategoryIds> response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<>() {
                });

        assertThat(response).hasSize(2);

        BookDtoWithoutCategoryIds theLordOfTheRings = response.stream().filter(b ->
                        b.getTitle().equals("The Lord of The Rings"))
                .toList().get(0);
        BookDtoWithoutCategoryIds gameOfThrones = response.stream().filter(b ->
                        b.getTitle().equals("A Game of Thrones"))
                .toList().get(0);
        assertNotNull(theLordOfTheRings);
        assertNotNull(gameOfThrones);
    }

    @WithMockUser
    @Test
    @DisplayName("Get books by non existing category id")
    void getBooksByCategoryId_NotValidId_ReturnsEmptyList() throws Exception {
        long nonExistingId = 100L;

        MvcResult result = mockMvc.perform(get("/api/categories/" + nonExistingId + "/books"))
                .andExpect(status().isOk())
                .andReturn();

        List<BookDtoWithoutCategoryIds> response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<>() {
                });

        assertTrue(response.isEmpty());
    }
}
