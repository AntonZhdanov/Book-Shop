package com.example.bookstore.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.bookstore.dto.book.BookDto;
import com.example.bookstore.dto.book.CreateBookRequestDto;
import com.example.bookstore.dto.category.CategoryDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
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
class BookControllerTest {
    protected static MockMvc mockMvc;
    private static BookDto expectedBookDto;
    private static CategoryDto sciFiDto;
    private static CreateBookRequestDto createBookRequestDto;
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
        setupDatabase(dataSource);
        setupTestData();
    }

    private static void setupDatabase(DataSource dataSource) throws SQLException {
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

    private static void setupTestData() {
        sciFiDto = new CategoryDto()
                .setId(2L)
                .setName("Science Fiction")
                .setDescription("Science Fiction");

        expectedBookDto = new BookDto()
                .setId(1L)
                .setTitle("1984")
                .setAuthor("George Orwell")
                .setIsbn("978-5-699-12014-7")
                .setPrice(BigDecimal.valueOf(19))
                .setDescription("Description")
                .setCoverImage("Cover image")
                .setCategoryIds(List.of(sciFiDto.getId()));

        createBookRequestDto = new CreateBookRequestDto(
                "The Hound of the Baskervilles",
                "Sir Arthur Conan Doyle",
                "9780544003415",
                BigDecimal.valueOf(15),
                "Description",
                "Cover image",
                List.of(3L));
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

    @WithMockUser
    @Test
    @DisplayName("Get all books")
    void getAll_ValidPageable_Success() throws Exception {
        MvcResult result = mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andReturn();

        List<BookDto> response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<>() {
                });

        assertThat(response).hasSize(4);
    }

    @WithMockUser
    @Test
    @DisplayName("Get book by id")
    void getBookById_ValidId_Success() throws Exception {
        long id = 1L;

        BookDto expected = BookControllerTest.expectedBookDto;

        MvcResult result = mockMvc.perform(get("/books/" + id))
                .andExpect(status().isOk())
                .andReturn();

        BookDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                BookDto.class);

        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @WithMockUser
    @Test
    @DisplayName("Get book by non existing id")
    void getBookById_NotValidId_NotFound() throws Exception {
        long nonExistingId = 100L;

        mockMvc.perform(get("/books/" + nonExistingId))
                .andExpect(status().isNotFound());
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @Sql(scripts = "classpath:database/books/remove-hound-of-baskervilles-book.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Create a book")
    void createBook_ValidRequestDto_Success() throws Exception {
        CreateBookRequestDto requestDto = createBookRequestDto;

        CategoryDto detectiveDto = new CategoryDto()
                .setId(3L)
                .setName("Detective")
                .setDescription("Detective");

        BookDto expected = new BookDto()
                .setTitle(requestDto.title())
                .setAuthor(requestDto.author())
                .setIsbn(requestDto.isbn())
                .setPrice(requestDto.price())
                .setDescription(requestDto.description())
                .setCoverImage(requestDto.coverImage())
                .setCategoryIds(List.of(detectiveDto.getId()));

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(
                        post("/books")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        BookDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                BookDto.class);

        assertNotNull(actual);
        assertNotNull(actual.getId());
        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Create a book with not valid request")
    void createBook_NotValidRequestDto_BadRequest() throws Exception {
        CreateBookRequestDto requestDto = new CreateBookRequestDto(

                "The Hound of the Baskervilles",
                "Sir Arthur Conan Doyle",
                "",
                null,
                "",
                "",
                null);

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(
                        post("/books")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Sql(scripts = "classpath:database/books/add-harry-potter1-book.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @DisplayName("Delete a book ")
    void delete_ValidId_Success() throws Exception {
        long id = 11L;

        MvcResult resultBeforeDelete = mockMvc.perform(get("/books")
                        .with(user("user")))
                .andExpect(status().isOk())
                .andReturn();

        List<BookDto> booksBeforeDelete = objectMapper.readValue(
                resultBeforeDelete.getResponse().getContentAsString(),
                new TypeReference<>() {
                });

        mockMvc.perform(delete("/books/" + id)
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk());

        MvcResult resultAfterDelete = mockMvc.perform(get("/books")
                        .with(user("user")))
                .andExpect(status().isOk())
                .andReturn();

        List<BookDto> booksAfterDelete = objectMapper.readValue(
                resultAfterDelete.getResponse().getContentAsString(),
                new TypeReference<>() {
                });

        assertThat(booksBeforeDelete).hasSize(5);
        assertThat(booksAfterDelete).hasSize(4);
    }

    @Test
    @DisplayName("Delete a book with not valid id")
    void delete_NotValidId() throws Exception {
        long nonExistingId = 100L;

        MvcResult resultBeforeDelete = mockMvc.perform(get("/books")
                        .with(user("user")))
                .andExpect(status().isOk())
                .andReturn();

        List<BookDto> booksBeforeDelete = objectMapper.readValue(
                resultBeforeDelete.getResponse().getContentAsString(),
                new TypeReference<>() {
                });

        mockMvc.perform(delete("/books/" + nonExistingId)
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk());

        MvcResult resultAfterDelete = mockMvc.perform(get("/books")
                        .with(user("user")))
                .andExpect(status().isOk())
                .andReturn();

        List<BookDto> booksAfterDelete = objectMapper.readValue(
                resultAfterDelete.getResponse().getContentAsString(),
                new TypeReference<>() {
                });

        assertThat(booksBeforeDelete).hasSize(4);
        assertThat(booksAfterDelete).hasSize(4);
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @Sql(scripts = "classpath:database/books/add-harry-potter2-book.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/remove-harry-potter2-book.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Update a book")
    void updateBookById_ValidIdValidRequestDto_Success() throws Exception {
        long id = 12L;

        CreateBookRequestDto requestDto = new CreateBookRequestDto(
                "1984",
                "George Orwell",
                "9780544003415",
                BigDecimal.valueOf(5),
                "Description",
                "Updated cover image",
                List.of(2L));

        CategoryDto sciFiDto = BookControllerTest.sciFiDto;

        BookDto expected = new BookDto()
                .setTitle(requestDto.title())
                .setAuthor(requestDto.author())
                .setIsbn(requestDto.isbn())
                .setPrice(requestDto.price())
                .setDescription(requestDto.description())
                .setCoverImage(requestDto.coverImage())
                .setCategoryIds(List.of(sciFiDto.getId()));

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(put("/books/" + id)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        BookDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                BookDto.class);

        assertNotNull(actual);
        assertNotNull(actual.getId());
        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Update a book with not valid id")
    void updateBookById_NotValidId_BadRequest() throws Exception {
        long nonExistingId = 100L;

        CreateBookRequestDto requestDto = new CreateBookRequestDto(
                "Updated Title",
                "Updated Author",
                "updated isbn",
                BigDecimal.valueOf(5),
                "Updated description",
                "Updated cover image",
                List.of(2L));

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(put("/books/" + nonExistingId)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Update a book with not valid request")
    void updateBookById_NotValidRequestDto_BadRequest() throws Exception {
        long id = 1L;

        CreateBookRequestDto requestDto = new CreateBookRequestDto(
                "Updated Title",
                "Updated Author",
                "",
                null,
                "",
                "",
                null);

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(put("/books/" + id)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Search books based on put criteria")
    void searchBooks_ValidParameters_ReturnsListOfBooks() throws Exception {
        MvcResult resultTitleQuery = mockMvc.perform(get("/books/search")
                        .param("title", "The Lord of The Rings")
                        .with(user("user")))
                .andExpect(status().isOk())
                .andReturn();

        MvcResult resultPriceQuery = mockMvc.perform(get("/books/search")
                        .param("price", "24")
                        .with(user("user")))
                .andExpect(status().isOk())
                .andReturn();

        MvcResult resultDescriptionQuery = mockMvc.perform(get("/books/search")
                        .param("description", "Description")
                        .with(user("user")))
                .andExpect(status().isOk())
                .andReturn();

        List<BookDto> responseTitleQuery = objectMapper.readValue(
                resultTitleQuery.getResponse().getContentAsString(),
                new TypeReference<>() {
                });

        List<BookDto> responsePriceQuery = objectMapper.readValue(
                resultPriceQuery.getResponse().getContentAsString(),
                new TypeReference<>() {
                });

        List<BookDto> responseDescriptionQuery = objectMapper.readValue(
                resultDescriptionQuery.getResponse().getContentAsString(),
                new TypeReference<>() {
                });

        assertFalse(responseTitleQuery.isEmpty());
        assertFalse(responsePriceQuery.isEmpty());
        assertFalse(responseDescriptionQuery.isEmpty());

        assertThat(responseTitleQuery).hasSize(1);
        assertThat(responsePriceQuery).hasSize(1);
        assertThat(responseDescriptionQuery).hasSize(4);
    }

    @WithMockUser
    @Test
    @DisplayName("Search books with no parameters")
    void searchBooks_NoParameters_ReturnsListOfAllBooks() throws Exception {
        MvcResult result = mockMvc.perform(get("/books/search"))
                .andExpect(status().isOk())
                .andReturn();

        List<BookDto> response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<>() {
                });

        assertFalse(response.isEmpty());
        assertThat(response).hasSize(4);
    }
}
