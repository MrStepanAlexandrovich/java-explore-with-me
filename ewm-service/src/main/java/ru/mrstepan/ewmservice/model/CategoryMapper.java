package ru.mrstepan.ewmservice.model;

import ru.mrstepan.ewmservice.dto.CategoryDto;

public class CategoryMapper {
    public static Category toCategory(CategoryDto categoryDto) {
        Category category = new Category();

        category.setId(categoryDto.getId());
        category.setName(categoryDto.getName());

        return category;
    }

    public static CategoryDto toDto(Category category) {
        CategoryDto categoryDto = new CategoryDto();

        categoryDto.setId(category.getId());
        categoryDto.setName(category.getName());

        return categoryDto;
    }
}
