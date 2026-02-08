package ru.mrstepan.ewmservice.controller.authorized;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.mrstepan.ewmservice.dto.RequestDto;
import ru.mrstepan.ewmservice.service.RequestService;

import java.util.Collection;

@RestController
@RequestMapping("/users/{userId}/requests")
@RequiredArgsConstructor
public class RequestController {
    private final RequestService requestService;

    @GetMapping
    public Collection<RequestDto> getRequests(@PathVariable long userId) {
        return requestService.getRequests(userId);
    }

    @PostMapping
    public void addRequest(
            @PathVariable long userId,
            @RequestParam(name = "eventId") long eventId)
    {
        requestService.addRequest(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public void cancelRequest(
            @PathVariable long userId,
            @PathVariable long requestId
    ) {
        requestService.cancelRequest(userId, requestId);
    }
}
