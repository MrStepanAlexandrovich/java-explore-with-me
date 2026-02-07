package ru.mrstepan.ewmservice.controller.admin;

import org.springframework.web.bind.annotation.*;
import ru.mrstepan.ewmservice.model.Compilation;

@RestController
@RequestMapping("/admin/compilations")
public class CompilationController {
    @PostMapping
    public void addCompilation(@RequestBody CompilationDto compilationDto) {

    }

    @DeleteMapping("/{id}")
    public void deleteCompilation(@PathVariable long id) {

    }

    @PatchMapping("/{id}")
    public void editCompilation(
            @PathVariable long id,
            @RequestBody CompilationEditDto compilationEditDto
    ) {

    }
}
