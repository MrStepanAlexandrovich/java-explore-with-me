package ru.mrstepan.ewmservice.controller.admin;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/categories")
public class CategoryController {

    @PostMapping
    public void addCategory(@RequestBody Category category) {

    }

    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable("id") long id) {

    }

    @PatchMapping("/{id}")
    public void editCategory(
            @PathVariable("id"),
            @RequestBody Category category
    ) {

    }
}
