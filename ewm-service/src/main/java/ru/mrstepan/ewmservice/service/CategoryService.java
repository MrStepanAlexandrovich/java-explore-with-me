package ru.mrstepan.ewmservice.service;

import ru.mrstepan.ewmservice.dto.CategoryDto;
import ru.mrstepan.ewmservice.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto addCategory(NewCategoryDto dto);

    void deleteCategory(long id);

    CategoryDto editCategory(long id, CategoryDto dto);

    List<CategoryDto> getCategories(int from, int size);

    CategoryDto getCategory(long id);
}
