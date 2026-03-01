package ru.mrstepan.ewmservice.controller.authorized;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.mrstepan.ewmservice.dto.RequestDto;
import ru.mrstepan.ewmservice.service.RequestService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/requests")
@RequiredArgsConstructor
@Validated
public class UserRequestController {
    private final RequestService requestService;

    @GetMapping
    public List<RequestDto> getRequests(@PathVariable @Positive long userId) {
        return requestService.getRequests(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RequestDto addRequest(@PathVariable @Positive long userId, @RequestParam @Positive long eventId) {
        return requestService.addRequest(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public RequestDto cancelRequest(@PathVariable @Positive long userId, @PathVariable @Positive long requestId) {
        return requestService.cancelRequest(userId, requestId);
    }
}
