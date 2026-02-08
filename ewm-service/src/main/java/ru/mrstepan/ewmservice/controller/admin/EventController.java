package ru.mrstepan.ewmservice.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.mrstepan.ewmservice.dto.EventEditDto;
import ru.mrstepan.ewmservice.model.Event;
import ru.mrstepan.ewmservice.service.EventService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    @GetMapping
    public Collection<Event> getEvents(
            @RequestParam List<Long> ids,
            @RequestParam long from,
            @RequestParam LocalDateTime rangeEnd,
            @RequestParam LocalDateTime rangeStart,
            @RequestParam long size,
            @RequestParam List<String> states,
            @RequestParam List<Integer> users
    ) {
        return eventService.getEvents(ids, from, rangeEnd, rangeStart, size, states, users);
    }

    @PatchMapping("/{id}")
    public void editEvent(
            @PathVariable long id,
            @RequestBody EventEditDto eventEditDto
    ) {
        eventService.editEventInfo(id, eventEditDto);
    }
}
