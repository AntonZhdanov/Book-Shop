package com.example.bookstore.dto.category;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class CategoryDto {
    private Long id;
    @NotEmpty
    private String name;
    private String description;
}
