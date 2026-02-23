package ru.mrstepan.ewmservice.controller.authorized;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.mrstepan.ewmservice.dto.*;
import ru.mrstepan.ewmservice.service.CommentService;
import ru.mrstepan.ewmservice.service.EventService;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
public class UserEventController {
    private final EventService eventService;
    private final CommentService commentService;

    @GetMapping
    public List<EventShortDto> getUsersEvents(
            @PathVariable long userId,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {
        return eventService.getUserEvents(userId, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addEvent(@PathVariable long userId, @Valid @RequestBody NewEventDto dto) {
        return eventService.addEvent(dto, userId);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventInfo(@PathVariable long userId, @PathVariable long eventId) {
        return eventService.getUserEvent(eventId, userId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto editEventInfo(@PathVariable long userId,
                                      @PathVariable long eventId,
                                      @RequestBody @Valid EventEditDto dto) {
        return eventService.updateUserEvent(userId, eventId, dto);
    }

    @GetMapping("/{eventId}/requests")
    public List<RequestDto> getRequests(@PathVariable long userId, @PathVariable long eventId) {
        return eventService.getEventRequests(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult editRequestStatus(
            @PathVariable long userId,
            @PathVariable long eventId,
            @RequestBody EventRequestStatusUpdateRequest request) {
        return eventService.changeRequestStatus(userId, eventId, request);
    }

    @GetMapping("/{eventId}/comments")
    public Collection<CommentResponseDto> getComments(@PathVariable @Positive long userId,
                                                      @PathVariable @Positive long eventId) {
        return commentService.getCommentsForEvent(eventId, userId);
    }

    @PostMapping("/{eventId}/comments")
    public CommentResponseDto addComment(@PathVariable @Positive long userId,
                                         @PathVariable @Positive long eventId,
                                         @RequestBody @Valid NewCommentDto commentDto) {
        return commentService.addComment(userId, commentDto, eventId);
    }

    @PatchMapping("/{eventId}/comments/{commentId}")
    public CommentResponseDto editComment(@PathVariable @Positive long userId,
                                          @PathVariable @Positive long eventId,
                                          @PathVariable @Positive long commentId,
                                          @RequestBody @Valid NewCommentDto commentDto) {
        return commentService.editComment(userId, eventId, commentId, commentDto);
    }

    @DeleteMapping("/{eventId}/comments/{commentId}")
    public void deleteComment(@PathVariable @Positive long userId,
                              @PathVariable @Positive long eventId,
                              @PathVariable @Positive long commentId) {
        commentService.deleteComment(userId, eventId, commentId);
    }
}
