package ru.mrstepan.ewmservice.controller.admin;

import org.springframework.web.bind.annotation.*;
import ru.mrstepan.ewmservice.model.Event;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/admin/events")
public class EventController {
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

    }

    @PatchMapping("/{id}")
    public void editEvent(
            @PathVariable long id,
            @RequestBody EventEditDto eventEditDto
    ) {

    }
}
