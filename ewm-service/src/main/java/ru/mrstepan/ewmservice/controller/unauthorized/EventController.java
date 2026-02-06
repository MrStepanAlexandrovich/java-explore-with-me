package ru.mrstepan.ewmservice.controller.unauthorized;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mrstepan.ewmservice.model.Event;

import java.util.Collection;

@RestController
@RequestMapping("/events")
public class EventController {
    public Collection<Event> getEvents() {

    }

    @GetMapping("/{id")
    public Collection<Event> getEventById(@PathVariable("id") long id) {

    }
}
