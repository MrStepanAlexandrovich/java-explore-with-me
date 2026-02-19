package ru.mrstepan.ewmservice.mapper;

import ru.mrstepan.ewmservice.dto.CategoryDto;
import ru.mrstepan.ewmservice.dto.EventFullDto;
import ru.mrstepan.ewmservice.dto.EventShortDto;
import ru.mrstepan.ewmservice.dto.NewEventDto;
import ru.mrstepan.ewmservice.dto.UserShortDto;
import ru.mrstepan.ewmservice.model.Category;
import ru.mrstepan.ewmservice.model.Event;
import ru.mrstepan.ewmservice.model.Status;
import ru.mrstepan.ewmservice.model.User;

public class EventMapper {
    public static Event toEvent(NewEventDto dto, Category category, User initiator) {
        Event event = new Event();
        event.setAnnotation(dto.getAnnotation());
        event.setCategory(category);
        event.setDescription(dto.getDescription());
        event.setEventDate(dto.getEventDate());
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
        dto.setCreatedOn(event.getCreatedOn() != null ? event.getCreatedOn() : null);
        dto.setDescription(event.getDescription());
        dto.setEventDate(event.getEventDate() != null ? event.getEventDate() : null);
        dto.setId(event.getId());
        dto.setInitiator(new UserShortDto(event.getInitiator().getId(), event.getInitiator().getName()));
        dto.setLocation(event.getLocation());
        dto.setPaid(event.getPaid());
        dto.setParticipantLimit(event.getParticipantLimit());
        dto.setPublishedOn(event.getPublishedOn() != null ? event.getPublishedOn() : null);
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
        dto.setEventDate(event.getEventDate() != null ? event.getEventDate() : null);
        dto.setId(event.getId());
        dto.setInitiator(new UserShortDto(event.getInitiator().getId(), event.getInitiator().getName()));
        dto.setPaid(event.getPaid());
        dto.setTitle(event.getTitle());
        dto.setViews(views);
        return dto;
    }
}
