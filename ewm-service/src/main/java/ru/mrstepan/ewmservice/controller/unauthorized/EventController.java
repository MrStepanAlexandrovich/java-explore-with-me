package ru.mrstepan.ewmservice.controller.unauthorized;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.mrstepan.ewmservice.dto.EventDto;
import ru.mrstepan.ewmservice.model.Event;
import ru.mrstepan.ewmservice.service.EventService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    public Collection<Event> getEvents(
            @RequestParam List<Integer> categories,
            @RequestParam long from,
            @RequestParam(defaultValue = "false") boolean onlyAvailable,
            @RequestParam boolean paid,
            @RequestParam LocalDateTime rangeEnd,
            @RequestParam LocalDateTime rangeStart,
            @RequestParam long size,
            @RequestParam String sort,
            @RequestParam String text
    ) {
        return eventService.getEvents(categories, from, rangeEnd, rangeStart, size, onlyAvailable, paid, sort, text);
    }

    @GetMapping("/{id}")
    public EventDto getEventById(@PathVariable("id") long id) {
        return eventService.getEvent(id);
    }
}
