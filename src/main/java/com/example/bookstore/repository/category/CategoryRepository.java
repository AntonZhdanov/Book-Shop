package com.example.bookstore.repository.category;

import com.example.bookstore.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query("SELECT c FROM Category c JOIN FETCH c.bookSet WHERE c.id = :id")
    Category findCategoryWithBooks(@Param("id")Long id);
}
