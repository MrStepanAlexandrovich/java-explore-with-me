package ru.mrstepan.ewmservice.controller.authorized;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.mrstepan.ewmservice.dto.EventDto;
import ru.mrstepan.ewmservice.dto.EventEditDto;
import ru.mrstepan.ewmservice.dto.RequestDto;
import ru.mrstepan.ewmservice.model.Event;
import ru.mrstepan.ewmservice.service.EventService;

import java.util.Collection;

@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    @GetMapping
    public Collection<EventDto> getUsersEvents(
            @PathVariable long userId,
            @RequestParam(name = "from") int from,
            @RequestParam(name = "size") int size
    ) {
        return eventService.getEvents(userId, from, size);
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
    public EventDto getEventInfo(
            @PathVariable long eventId,
            @PathVariable long userId
    ) {
        return eventService.getEvent(eventId, userId);
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
        return eventService.getRequests(eventId, userId);
    }

    @PatchMapping("/{eventId}/requests")
    public void editRequestStatus(
            @PathVariable long eventId,
            @PathVariable long userId
    ) {
        eventService.editRequestStatus(eventId, userId);
    }
}
