package ru.mrstepan.ewmservice.controller.unauthorized;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.mrstepan.ewmservice.dto.CompilationDto;
import ru.mrstepan.ewmservice.service.CompilationService;

import java.util.Collection;

@RestController
@RequestMapping("/compilations")
@RequiredArgsConstructor
public class CompilationController {
    private final CompilationService compilationService;

    @GetMapping
    public Collection<CompilationDto> getEventsCompilations(
            @RequestParam(name = "from") int from,
            @RequestParam(name = "pinned") boolean pinned,
            @RequestParam(name = "size") int size
    ) {
        return compilationService.getCompilations(from, pinned, size);
    }

    @GetMapping("/{id}")
    public CompilationDto getEventsCompilationById(
            @PathVariable("id") long id
    ) {
        return compilationService.getCompilationById(id);
    }
}
