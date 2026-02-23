package ru.mrstepan.ewmservice.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.mrstepan.ewmservice.dao.CategoryRepository;
import ru.mrstepan.ewmservice.dao.EventRepository;
import ru.mrstepan.ewmservice.dto.CategoryDto;
import ru.mrstepan.ewmservice.dto.NewCategoryDto;
import ru.mrstepan.ewmservice.exception.ConflictException;
import ru.mrstepan.ewmservice.exception.NotFoundException;
import ru.mrstepan.ewmservice.model.Category;
import ru.mrstepan.ewmservice.model.Event;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category category;
    private NewCategoryDto newCategoryDto;
    private CategoryDto categoryDto;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(1L);
        category.setName("Test Category");

        newCategoryDto = new NewCategoryDto();
        newCategoryDto.setName("Test Category");

        categoryDto = new CategoryDto(1L, "Test Category");
    }

    @Test
    void addCategory_Success() {
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        CategoryDto result = categoryService.addCategory(newCategoryDto);

        assertNotNull(result);
        assertEquals("Test Category", result.getName());
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void addCategory_DuplicateName_ThrowsConflict() {
        when(categoryRepository.save(any(Category.class)))
                .thenThrow(new DataIntegrityViolationException("Duplicate"));

        assertThrows(ConflictException.class, () ->
                categoryService.addCategory(newCategoryDto));
    }

    @Test
    void deleteCategory_Success() {
        when(categoryRepository.existsById(1L)).thenReturn(true);
        when(eventRepository.findAllByCategory_Id(1L)).thenReturn(List.of());

        assertDoesNotThrow(() -> categoryService.deleteCategory(1L));

        verify(categoryRepository).deleteById(1L);
    }

    @Test
    void deleteCategory_NotFound() {
        when(categoryRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(NotFoundException.class, () ->
                categoryService.deleteCategory(999L));

        verify(categoryRepository, never()).deleteById(anyLong());
    }

    @Test
    void deleteCategory_HasEvents_ThrowsConflict() {
        when(categoryRepository.existsById(1L)).thenReturn(true);
        when(eventRepository.findAllByCategory_Id(1L)).thenReturn(List.of(new Event()));

        assertThrows(ConflictException.class, () ->
                categoryService.deleteCategory(1L));

        verify(categoryRepository, never()).deleteById(anyLong());
    }

    @Test
    void editCategory_Success() {
        CategoryDto updateDto = new CategoryDto(1L, "Updated Category");
        Category updatedCategory = new Category();
        updatedCategory.setId(1L);
        updatedCategory.setName("Updated Category");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class))).thenReturn(updatedCategory);

        CategoryDto result = categoryService.editCategory(1L, updateDto);

        assertNotNull(result);
        assertEquals("Updated Category", result.getName());
    }

    @Test
    void editCategory_NotFound() {
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                categoryService.editCategory(999L, categoryDto));
    }

    @Test
    void editCategory_DuplicateName_ThrowsConflict() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class)))
                .thenThrow(new DataIntegrityViolationException("Duplicate"));

        assertThrows(ConflictException.class, () ->
                categoryService.editCategory(1L, categoryDto));
    }

    @Test
    void getCategories_Success() {
        Page<Category> categoryPage = new PageImpl<>(List.of(category));

        when(categoryRepository.findAll(any(PageRequest.class))).thenReturn(categoryPage);

        List<CategoryDto> result = categoryService.getCategories(0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Category", result.get(0).getName());
    }

    @Test
    void getCategory_Success() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        CategoryDto result = categoryService.getCategory(1L);

        assertNotNull(result);
        assertEquals("Test Category", result.getName());
    }

    @Test
    void getCategory_NotFound() {
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                categoryService.getCategory(999L));
    }
}
