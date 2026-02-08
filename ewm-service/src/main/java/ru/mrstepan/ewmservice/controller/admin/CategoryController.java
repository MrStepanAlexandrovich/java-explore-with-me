package ru.mrstepan.ewmservice.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.mrstepan.ewmservice.dto.CategoryDto;
import ru.mrstepan.ewmservice.service.CategoryService;

@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void addCategory(@RequestBody CategoryDto categoryDto) {
        categoryService.addCategory(categoryDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteCategory(@PathVariable("id") long id) {
        categoryService.deleteCategory(id);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void editCategory(
            @PathVariable("id") long id,
            @RequestBody CategoryDto categoryDto
    ) {
        categoryService.editCategory(id, categoryDto);
    }
}
