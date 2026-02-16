package ru.mrstepan.ewmservice.model;

import ru.mrstepan.ewmservice.dto.CategoryDto;
import ru.mrstepan.ewmservice.dto.EventFullDto;
import ru.mrstepan.ewmservice.dto.EventShortDto;
import ru.mrstepan.ewmservice.dto.NewEventDto;
import ru.mrstepan.ewmservice.dto.UserShortDto;

import java.time.format.DateTimeFormatter;

public class EventMapper {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static Event toEvent(NewEventDto dto, Category category, User initiator) {
        Event event = new Event();
        event.setAnnotation(dto.getAnnotation());
        event.setCategory(category);
        event.setDescription(dto.getDescription());
        event.setEventDate(java.time.LocalDateTime.parse(dto.getEventDate(), FORMATTER));
        event.setLocation(dto.getLocation());
        event.setPaid(dto.getPaid() != null ? dto.getPaid() : false);
        event.setParticipantLimit(dto.getParticipantLimit() != null ? dto.getParticipantLimit() : 0);
        event.setRequestModeration(dto.getRequestModeration() != null ? dto.getRequestModeration() : true);
        event.setTitle(dto.getTitle());
        event.setInitiator(initiator);
        event.setState(Status.PENDING);
        event.setCreatedOn(java.time.LocalDateTime.now());
        return event;
    }

    public static EventFullDto toFullDto(Event event, long confirmedRequests, long views) {
        EventFullDto dto = new EventFullDto();
        dto.setAnnotation(event.getAnnotation());
        dto.setCategory(new CategoryDto(event.getCategory().getId(), event.getCategory().getName()));
        dto.setConfirmedRequests(confirmedRequests);
        dto.setCreatedOn(event.getCreatedOn() != null ? event.getCreatedOn().format(FORMATTER) : null);
        dto.setDescription(event.getDescription());
        dto.setEventDate(event.getEventDate() != null ? event.getEventDate().format(FORMATTER) : null);
        dto.setId(event.getId());
        dto.setInitiator(new UserShortDto(event.getInitiator().getId(), event.getInitiator().getName()));
        dto.setLocation(event.getLocation());
        dto.setPaid(event.getPaid());
        dto.setParticipantLimit(event.getParticipantLimit());
        dto.setPublishedOn(event.getPublishedOn() != null ? event.getPublishedOn().format(FORMATTER) : null);
        dto.setRequestModeration(event.getRequestModeration());
        dto.setState(event.getState() != null ? event.getState().name() : null);
        dto.setTitle(event.getTitle());
        dto.setViews(views);
        return dto;
    }

    public static EventShortDto toShortDto(Event event, long confirmedRequests, long views) {
        EventShortDto dto = new EventShortDto();
        dto.setAnnotation(event.getAnnotation());
        dto.setCategory(new CategoryDto(event.getCategory().getId(), event.getCategory().getName()));
        dto.setConfirmedRequests(confirmedRequests);
        dto.setEventDate(event.getEventDate() != null ? event.getEventDate().format(FORMATTER) : null);
        dto.setId(event.getId());
        dto.setInitiator(new UserShortDto(event.getInitiator().getId(), event.getInitiator().getName()));
        dto.setPaid(event.getPaid());
        dto.setTitle(event.getTitle());
        dto.setViews(views);
        return dto;
    }
}
