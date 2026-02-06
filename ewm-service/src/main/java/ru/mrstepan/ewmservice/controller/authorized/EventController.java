package ru.mrstepan.ewmservice.controller.authorized;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.mrstepan.ewmservice.model.Event;
import ru.mrstepan.ewmservice.service.EventService;

import java.util.Collection;

@RestController
@RequestMapping("/users/{userId}/events")
public class EventController {
    private final EventService eventService;

    @GetMapping
    public Collection<Event> getUsersEvents(
            @PathVariable long userId,
            @RequestParam(name = "from") int from,
            @RequestParam(name = "size") int size
    ) {
        eventService.getEvents(userId, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void addEvent(
            @RequestBody EventDto eventDto,
            @PathVariable long userId
    ) {
        eventService.addEvent(eventDto, userId);
    }

    @GetMapping("/{eventId}")
    public void getEventInfo(
            @PathVariable long eventId,
            @PathVariable long userId
    ) {
        eventService.getEvent(eventId, userId);
    }

    @PatchMapping("/{eventId}")
    public void editEventInfo(
            @RequestBody EventEditDto eventEditDto,
            @PathVariable long eventId,
            @PathVariable long userId
    ) {
        eventService.editEventInfo(eventEditDto, eventId, userId);
    }

    @GetMapping("/{eventId}/requests")
    public Collection<RequestDto> getRequests(
            @PathVariable long eventId,
            @PathVariable long userId
    ) {
        eventService.getRequests(eventId, userId);
    }

    @PatchMapping("/{eventId}/requests")
    public void editRequestStatus(
            @PathVariable long eventId,
            @PathVariable long userId
    ) {
        eventService.editRequestStatus(eventId, userId);
    }
}
