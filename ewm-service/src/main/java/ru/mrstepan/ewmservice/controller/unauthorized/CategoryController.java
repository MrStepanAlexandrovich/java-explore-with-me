package ru.mrstepan.ewmservice.controller.unauthorized;


import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/categories")
public class CategoryController {
    @GetMapping
    public Collection<Category> getCategories(
            @RequestParam(name = "from") int from,
            @RequestParam(name = "size") int size
    ) {

    }

    @GetMapping("/{id}")
    public Category getCategory(@PathVariable long id) {

    }
}
