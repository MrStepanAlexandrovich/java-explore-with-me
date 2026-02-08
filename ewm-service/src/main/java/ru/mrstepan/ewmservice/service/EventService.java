package ru.mrstepan.ewmservice.service;

import ru.mrstepan.ewmservice.dto.EventDto;
import ru.mrstepan.ewmservice.dto.EventEditDto;
import ru.mrstepan.ewmservice.dto.RequestDto;
import ru.mrstepan.ewmservice.model.Event;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface EventService {

    Collection<EventDto> getEvents(long userId, int from, int size);

    Collection<Event> getEvents(List<Long> ids, long from, LocalDateTime rangeEnd, LocalDateTime rangeStart, long size,
                                List<String> states, List<Integer> users);

    Collection<Event> getEvents(List<Integer> categories, long from, LocalDateTime rangeEnd, LocalDateTime rangeStart,
                                long size, boolean onlyAvailable, boolean paid, String sort, String text);

    void addEvent(EventDto eventDto, long userId);

    void editRequestStatus(long eventId, long userId);

    EventDto getEvent(long eventId, long userId);

    EventDto getEvent(long eventId);

    void editEventInfo(EventEditDto eventEditDto, long eventId, long userId);

    void editEventInfo(long id, EventEditDto eventEditDto);

    Collection<RequestDto> getRequests(long eventId, long userId);

}
