package ru.mrstepan.ewmservice.controller.unauthorized;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.mrstepan.ewmservice.dto.CategoryDto;
import ru.mrstepan.ewmservice.service.CategoryService;

import java.util.Collection;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public Collection<CategoryDto> getCategories(
            @RequestParam(name = "from") int from,
            @RequestParam(name = "size") int size
    ) {
        return categoryService.getCategories(from, size);
    }

    @GetMapping("/{id}")
    public CategoryDto getCategory(@PathVariable long id) {
        return categoryService.getCategory(id);
    }
}
