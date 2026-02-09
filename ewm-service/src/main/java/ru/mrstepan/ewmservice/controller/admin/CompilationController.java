package ru.mrstepan.ewmservice.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.mrstepan.ewmservice.dto.CompilationDto;
import ru.mrstepan.ewmservice.dto.CompilationEditDto;
import ru.mrstepan.ewmservice.service.CompilationService;

@RestController
@RequestMapping("/admin/compilations")
@RequiredArgsConstructor
public class CompilationController {
    private final CompilationService compilationService;

    @PostMapping
    public void addCompilation(@RequestBody CompilationDto compilationDto) {
        compilationService.addCompilation(compilationDto);
    }

    @DeleteMapping("/{id}")
    public void deleteCompilation(@PathVariable long id) {
        compilationService.deleteCompilation(id);
    }

    @PatchMapping("/{id}")
    public void editCompilation(
            @PathVariable long id,
            @RequestBody CompilationEditDto compilationEditDto
    ) {
        compilationService.editCompilation(id, compilationEditDto);
    }
}
