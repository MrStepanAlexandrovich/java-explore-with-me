package ru.mrstepan.ewmservice.controller.authorized;

import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/users/{userId}/requests")
public class RequestController {
    @GetMapping
    public Collection<RequestDto> getRequests(@PathVariable long userId) {

    }

    @PostMapping
    public void addRequest(
            @PathVariable long userId,
            @RequestParam(name = "eventId") long eventId)
    {

    }

    @PatchMapping
    public void cancelRequest(
            @PathVariable long userId,
            @PathVariable long requestId
    ) {

    }
}
