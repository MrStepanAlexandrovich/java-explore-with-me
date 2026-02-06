package ru.mrstepan.ewmservice.controller.unauthorized;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.mrstepan.ewmservice.service.CompilationService;

@RestController
@RequestMapping("/compilations")
@RequiredArgsConstructor
public class CompilationController {
    private final CompilationService compilationService;

    @GetMapping
    public void getEventsCompilations(
            @RequestParam(name = "from") int from,
            @RequestParam(name = "pinned") boolean pinned,
            @RequestParam(name = "size") int size
    ) {
        compilationService.getCompilations(from, pinned, size);
    }

    @GetMapping("/{id}")
    public void getEventsCompilationsById(
            @PathVariable("id") long id
    ) {

    }
}
