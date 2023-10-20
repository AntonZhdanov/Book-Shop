package com.example.bookstore.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.bookstore.model.ShoppingCart;
import com.example.bookstore.model.User;
import com.example.bookstore.repository.shoppingcart.ShoppingCartRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ShoppingCartRepositoryTest {
    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void findByUserId_ExistingUserId_ReturnsShoppingCart() {
        User user = new User();
        user.setEmail(UUID.randomUUID() + "@example.com");
        user.setPassword("securePassword");
        user.setFirstName("John");
        user.setLastName("Doe");

        entityManager.persist(user);

        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);
        entityManager.persist(shoppingCart);

        Optional<ShoppingCart> foundShoppingCart =
                shoppingCartRepository.findByUserId(user.getId());

        assertTrue(foundShoppingCart.isPresent());
        assertEquals(shoppingCart.getId(), foundShoppingCart.get().getId());
    }

    @Test
    public void findByUserId_NonExistingUserId_ReturnsEmpty() {
        Long nonExistingUserId = 999L;

        Optional<ShoppingCart> foundShoppingCart =
                shoppingCartRepository.findByUserId(nonExistingUserId);

        assertFalse(foundShoppingCart.isPresent());
    }
}
