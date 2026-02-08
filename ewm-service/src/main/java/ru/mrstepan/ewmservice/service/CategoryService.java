package ru.mrstepan.ewmservice.service;

import ru.mrstepan.ewmservice.dto.CategoryDto;

import java.util.Collection;

public interface CategoryService {
    void addCategory(CategoryDto categoryDto);

    void deleteCategory(long id);

    void editCategory(long id, CategoryDto categoryDto);

    Collection<CategoryDto> getCategories(int from, int size);

    CategoryDto getCategory(long id);
}
