package com.example.bookstore.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.example.bookstore.dto.category.CategoryDto;
import com.example.bookstore.exception.EntityNotFoundException;
import com.example.bookstore.mapper.CategoryMapper;
import com.example.bookstore.model.Category;
import com.example.bookstore.repository.category.CategoryRepository;
import com.example.bookstore.service.category.CategoryServiceImpl;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {
    static final Long CATEGORY_ID = 1L;
    static final String CATEGORY_NAME = "Fantasy";
    static final String CATEGORY_DESCRIPTION = "Fantasy";

    @InjectMocks
    private CategoryServiceImpl categoryService;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CategoryMapper categoryMapper;

    private Category fantasyCategory;
    private CategoryDto fantasyCategoryDto;

    @BeforeEach
    void setUp() {
        fantasyCategory = createCategory(CATEGORY_ID, CATEGORY_NAME, CATEGORY_DESCRIPTION);
        fantasyCategoryDto = createCategoryDto(CATEGORY_ID, CATEGORY_NAME, CATEGORY_DESCRIPTION);
    }

    private Category createCategory(Long id, String name, String description) {
        Category category = new Category();
        category.setId(id);
        category.setName(name);
        category.setDescription(description);
        return category;
    }

    private CategoryDto createCategoryDto(Long id, String name, String description) {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(id);
        categoryDto.setName(name);
        categoryDto.setDescription(description);
        return categoryDto;
    }

    @Test
    @DisplayName("Find all categories")
    void findAll_ValidPageable_ReturnsAllCategories() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Category> categories = List.of(fantasyCategory);
        Page<Category> categoryPage = new PageImpl<>(categories, pageable, categories.size());

        when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);
        when(categoryMapper.toDto(any(Category.class))).thenReturn(fantasyCategoryDto);

        List<CategoryDto> actualAllCategories = categoryService.findAll(pageable);

        assertThat(actualAllCategories).hasSize(1);
        assertEquals(fantasyCategoryDto, actualAllCategories.get(0));

        verify(categoryRepository, times(1)).findAll(pageable);
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("Get category by id")
    void getById_ValidCategoryId_ReturnsCategoryDto() {
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(fantasyCategory));
        when(categoryMapper.toDto(any(Category.class))).thenReturn(fantasyCategoryDto);

        CategoryDto actual = categoryService.getById(CATEGORY_ID);

        assertEquals(fantasyCategoryDto, actual);
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("Get category with not valid id")
    void getById_NotValidCategoryId_ThrowsException() {
        Long nonExistingId = 100L;

        when(categoryRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> categoryService.getById(nonExistingId));

        String expectedMessage = "Can't find category by id: " + nonExistingId;
        assertEquals(expectedMessage, exception.getMessage());
        verify(categoryRepository).findById(nonExistingId);
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    @DisplayName("Update category")
    void updateById_ValidCategoryId_ReturnsUpdatedCategoryDto() {
        CategoryDto request = createCategoryDto(null, "Updated Fantasy",
                "Updated Fantasy");
        Category categoryFromDb = createCategory(1L, "Fantasy",
                "Fantasy");
        Category updatedCategory = createCategory(1L, "Updated Fantasy",
                "Updated Fantasy");

        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(categoryFromDb));
        when(categoryMapper.toModel(request)).thenReturn(updatedCategory);
        when(categoryRepository.save(any(Category.class))).thenReturn(updatedCategory);
        when(categoryMapper.toDto(updatedCategory)).thenReturn(createCategoryDto(1L,
                "Updated Fantasy", "Updated Fantasy"));

        CategoryDto updatedDto = categoryService.update(1L, request);

        assertEquals(request.getName(), updatedDto.getName());
        assertEquals(request.getDescription(), updatedDto.getDescription());
        verify(categoryRepository).save(updatedCategory);
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("Delete category by id")
    void deleteById_ShouldCallRepositoryDelete() {
        Long categoryId = 1L;

        categoryService.deleteById(categoryId);

        verify(categoryRepository).deleteById(categoryId);
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    @DisplayName("Save a category")
    void shouldSaveCategory() {
        CategoryDto categoryDto = createCategoryDto(null,
                "Science Fiction", "Description");
        Category category = createCategory(null,
                "Science Fiction", "Description");
        Category savedCategory = createCategory(1L,
                "Science Fiction", "Description");

        when(categoryMapper.toModel(categoryDto)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(savedCategory);
        when(categoryMapper.toDto(savedCategory)).thenReturn(createCategoryDto(1L,
                "Science Fiction", "Description"));

        CategoryDto resultDto = categoryService.save(categoryDto);

        assertEquals(categoryDto.getName(), resultDto.getName());
        assertEquals(categoryDto.getDescription(), resultDto.getDescription());
        assertEquals(1L, resultDto.getId());

        verify(categoryRepository).save(category);
    }

    @Test
    @DisplayName("Update category with non-existing id throws EntityNotFoundException")
    void updateById_NonExistingId_ThrowsEntityNotFoundException() {
        final Long nonExistingId = 999L;
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setName("SomeName");
        categoryDto.setDescription("SomeDescription");

        Category mockCategory = new Category();
        when(categoryMapper.toModel(any(CategoryDto.class))).thenReturn(mockCategory);
        when(categoryRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, ()
                -> categoryService.update(nonExistingId, categoryDto));
    }
}
