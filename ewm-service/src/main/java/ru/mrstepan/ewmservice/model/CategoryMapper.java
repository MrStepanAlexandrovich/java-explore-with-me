package ru.mrstepan.ewmservice.model;

import ru.mrstepan.ewmservice.dto.CategoryDto;
import ru.mrstepan.ewmservice.dto.NewCategoryDto;

public class CategoryMapper {
    public static Category toCategory(NewCategoryDto dto) {
        Category category = new Category();
        category.setName(dto.getName());
        return category;
    }

    public static Category toCategory(CategoryDto dto) {
        Category category = new Category();
        category.setName(dto.getName());
        return category;
    }

    public static CategoryDto toDto(Category category) {
        return new CategoryDto(category.getId(), category.getName());
    }
}
