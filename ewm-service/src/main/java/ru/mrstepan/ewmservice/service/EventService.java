package ru.mrstepan.ewmservice.service;

public interface EventService {

    void getEvents(long userId, int from, int size);

    void addEvent(EventDto eventDto, long userId);

    void editRequestStatus(long eventId, long userId);

    void getEvent(long eventId, long userId);

    void editEventInfo(EventEditDto eventEditDto, long eventId, long userId);

    void getRequests(long eventId, long userId);
}
