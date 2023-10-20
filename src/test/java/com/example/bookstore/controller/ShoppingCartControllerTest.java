package com.example.bookstore.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.bookstore.dto.cartitem.CartItemDto;
import com.example.bookstore.dto.cartitem.CreateCartItemRequestDto;
import com.example.bookstore.dto.cartitem.UpdateQuantityInCartItemDto;
import com.example.bookstore.dto.shoppingcart.ShoppingCartDto;
import com.example.bookstore.security.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
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
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ShoppingCartControllerTest {
    protected static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JwtUtil jwtUtil;

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
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/users/create-user.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/roles/create-roles.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/roles/add-roles-for-users.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/shoppingcarts/create-shopping-cart.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/cartitems/add-1984-to-cartitems.sql"));
        }
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
                    new ClassPathResource("database/cartitems/delete-1984-from-cartitems.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/shoppingcarts/delete-shopping-cart.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/roles/remove-roles-from-users.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/roles/delete-roles.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/users/delete-user.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/categories/remove-categories-from-books.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/categories/remove-categories.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/books/remove-books.sql"));
        }
    }

    @Test
    @DisplayName("Get shopping cart for user")
    void getShoppingCartForUser_AuthenticatedUser_Success() throws Exception {
        CartItemDto item1 = new CartItemDto()
                .setId(1L)
                .setBookId(1L)
                .setBookTitle("1984")
                .setQuantity(2);

        ShoppingCartDto expected = new ShoppingCartDto()
                .setId(1L)
                .setUserId(1L)
                .setCartItemDtoSet(new HashSet<>(List.of(item1)));

        String token = jwtUtil.generateToken("johndoe@mail.com");

        MvcResult result = mockMvc.perform(get("/api/cart")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        ShoppingCartDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                ShoppingCartDto.class);

        assertNotNull(actual);
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getUserId(), actual.getUserId());
        assertThat(actual.getCartItemDtoSet()).hasSize(1);

        CartItemDto actualItem = actual.getCartItemDtoSet().iterator().next();
        assertEquals(item1.getId(), actualItem.getId());
        assertEquals(item1.getBookId(), actualItem.getBookId());
        assertEquals(item1.getBookTitle(), actualItem.getBookTitle());
        assertEquals(item1.getQuantity(), actualItem.getQuantity());
    }

    @Test
    @Sql(scripts = "classpath:database/cartitems/delete-lord-of-the-rings-from-cartitems.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Add item to shopping cart")
    void addToShoppingCart_ValidRequestDto_Success() throws Exception {
        String token = jwtUtil.generateToken("johndoe@mail.com");

        Long bookId = 2L;
        CreateCartItemRequestDto itemRequestDto = new CreateCartItemRequestDto()
                .setBookId(bookId)
                .setQuantity(5);

        String jsonRequest = objectMapper.writeValueAsString(itemRequestDto);

        MvcResult resultBeforeAdd = mockMvc.perform(get("/api/cart")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        ShoppingCartDto cartBeforeAdd = objectMapper.readValue(
                resultBeforeAdd.getResponse().getContentAsString(),
                ShoppingCartDto.class);

        mockMvc.perform(post("/api/cart")
                        .header("Authorization", "Bearer " + token)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        MvcResult resultAfterAdd = mockMvc.perform(get("/api/cart")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        ShoppingCartDto cartAfterAdd = objectMapper.readValue(
                resultAfterAdd.getResponse().getContentAsString(),
                ShoppingCartDto.class);

        Set<CartItemDto> newItem = cartAfterAdd.getCartItemDtoSet().stream()
                .filter(i -> i.getBookId().equals(bookId))
                .collect(Collectors.toSet());

        assertFalse(newItem.isEmpty());
        assertThat(cartBeforeAdd.getCartItemDtoSet()).hasSize(1);
        assertThat(cartAfterAdd.getCartItemDtoSet()).hasSize(2);
    }

    @Test
    @Sql(scripts = "classpath:database/cartitems/add-game-of-thrones-to-cartitems.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/cartitems/delete-game-of-thrones-from-cartitems.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Update item quantity")
    void updateItemQuantity_ValidItemId_Success() throws Exception {
        String token = jwtUtil.generateToken("johndoe@mail.com");
        Long cartItemId = 3L;
        int expectedInitialQuantity = 1;
        int expectedNewQuantity = 10;

        UpdateQuantityInCartItemDto updateQuantityDto =
                new UpdateQuantityInCartItemDto(expectedNewQuantity);

        String jsonRequest = objectMapper.writeValueAsString(updateQuantityDto);

        MvcResult resultBeforeUpdate = mockMvc.perform(get("/api/cart")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        ShoppingCartDto cartBeforeUpdate = objectMapper.readValue(resultBeforeUpdate
                .getResponse().getContentAsString(), ShoppingCartDto.class);

        CartItemDto itemBeforeUpdate = cartBeforeUpdate.getCartItemDtoSet().stream()
                .filter(i -> i.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new AssertionError("CartItem with ID "
                        + cartItemId + " not found"));

        int quantityBeforeUpdate = itemBeforeUpdate.getQuantity();

        mockMvc.perform(put("/api/cart/cart-items/" + cartItemId)
                        .header("Authorization", "Bearer " + token)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        MvcResult resultAfterUpdate = mockMvc.perform(get("/api/cart")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        ShoppingCartDto cartAfterUpdate = objectMapper.readValue(resultAfterUpdate
                .getResponse().getContentAsString(), ShoppingCartDto.class);

        CartItemDto itemAfterUpdate = cartAfterUpdate.getCartItemDtoSet().stream()
                .filter(i -> i.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new AssertionError("CartItem with ID "
                        + cartItemId + " not found"));

        int quantityAfterUpdate = itemAfterUpdate.getQuantity();

        assertEquals(expectedInitialQuantity, quantityBeforeUpdate);
        assertEquals(expectedNewQuantity, quantityAfterUpdate);
    }

    @Test
    @DisplayName("Update item quantity with not valid item id")
    void updateItemQuantity_NotValidItemId_NotFound() throws Exception {
        String token = jwtUtil.generateToken("johndoe@mail.com");
        long nonExistingid = 100L;

        UpdateQuantityInCartItemDto updateQuantityDto = new UpdateQuantityInCartItemDto(10);

        String jsonRequest = objectMapper.writeValueAsString(updateQuantityDto);

        mockMvc.perform(put("/api/cart/cart-items/" + nonExistingid)
                        .header("Authorization", "Bearer " + token)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @Sql(scripts = "classpath:database/cartitems/add-sherlock-holmes-to-cartitems.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/cartitems/delete-sherlock-holmes-from-cartitems.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Delete item from shopping cart")
    void deleteItem_ValidId_Success() throws Exception {
        String token = jwtUtil.generateToken("johndoe@mail.com");
        Long id = 2L;

        MvcResult resultBeforeDelete = mockMvc.perform(get("/api/cart")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        ShoppingCartDto cartBeforeDelete = objectMapper.readValue(
                resultBeforeDelete.getResponse().getContentAsString(),
                ShoppingCartDto.class);

        List<CartItemDto> itemBeforeDelete = cartBeforeDelete.getCartItemDtoSet().stream()
                .filter(i -> i.getId().equals(id))
                .toList();

        mockMvc.perform(delete("/api/cart/cart-items/" + id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        MvcResult resultAfterDelete = mockMvc.perform(get("/api/cart")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        ShoppingCartDto cartAfterDelete = objectMapper.readValue(
                resultAfterDelete.getResponse().getContentAsString(),
                ShoppingCartDto.class);

        List<CartItemDto> itemAfterDelete = cartAfterDelete.getCartItemDtoSet().stream()
                .filter(i -> i.getId().equals(id))
                .toList();

        assertFalse(itemBeforeDelete.isEmpty());
        assertTrue(itemAfterDelete.isEmpty());
        assertEquals(2, cartBeforeDelete.getCartItemDtoSet().size());
        assertEquals(1, cartAfterDelete.getCartItemDtoSet().size());
    }

    @Test
    @DisplayName("Delete item from shopping cart with not valid item id")
    void deleteItem_NotValidId_NoContent() throws Exception {
        String token = jwtUtil.generateToken("johndoe@mail.com");
        long nonexistent = 100L;

        mockMvc.perform(delete("/api/cart/cart-items/" + nonexistent)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }
}
