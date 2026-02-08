package ru.mrstepan.ewmservice.service;

import org.springframework.stereotype.Service;
import ru.mrstepan.ewmservice.dto.EventDto;
import ru.mrstepan.ewmservice.dto.EventEditDto;
import ru.mrstepan.ewmservice.dto.RequestDto;
import ru.mrstepan.ewmservice.model.Event;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Service
public class EventServiceImpl implements EventService {
    @Override
    public Collection<EventDto> getEvents(long userId, int from, int size) {
        return List.of();
    }

    @Override
    public Collection<Event> getEvents(List<Long> ids, long from, LocalDateTime rangeEnd, LocalDateTime rangeStart, long size, List<String> states, List<Integer> users) {
        return List.of();
    }

    @Override
    public Collection<Event> getEvents(List<Integer> categories, long from, LocalDateTime rangeEnd, LocalDateTime rangeStart, long size, boolean onlyAvailable, boolean paid, String sort, String text) {
        return List.of();
    }

    @Override
    public void addEvent(EventDto eventDto, long userId) {

    }

    @Override
    public void editRequestStatus(long eventId, long userId) {

    }

    @Override
    public EventDto getEvent(long eventId, long userId) {
        return null;
    }

    @Override
    public void editEventInfo(EventEditDto eventEditDto, long eventId, long userId) {

    }

    @Override
    public EventDto getEvent(long eventId) {
        return null;
    }

    @Override
    public void editEventInfo(long id, EventEditDto eventEditDto) {

    }

    @Override
    public Collection<RequestDto> getRequests(long eventId, long userId) {
        return List.of();
    }
}
