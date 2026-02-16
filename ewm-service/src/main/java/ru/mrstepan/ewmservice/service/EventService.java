package ru.mrstepan.ewmservice.service;

import ru.mrstepan.ewmservice.dto.EventEditDto;
import ru.mrstepan.ewmservice.dto.EventFullDto;
import ru.mrstepan.ewmservice.dto.EventRequestStatusUpdateRequest;
import ru.mrstepan.ewmservice.dto.EventRequestStatusUpdateResult;
import ru.mrstepan.ewmservice.dto.EventShortDto;
import ru.mrstepan.ewmservice.dto.NewEventDto;
import ru.mrstepan.ewmservice.dto.RequestDto;

import java.util.List;

public interface EventService {

    // Private: получение событий пользователя
    List<EventShortDto> getUserEvents(long userId, int from, int size);

    // Private: добавить событие
    EventFullDto addEvent(NewEventDto dto, long userId);

    // Private: получить событие пользователя
    EventFullDto getUserEvent(long eventId, long userId);

    // Private: обновить событие пользователем
    EventFullDto updateUserEvent(long userId, long eventId, EventEditDto dto);

    // Private: получить заявки на участие в событии
    List<RequestDto> getEventRequests(long userId, long eventId);

    // Private: изменить статус заявок
    EventRequestStatusUpdateResult changeRequestStatus(long userId, long eventId, EventRequestStatusUpdateRequest request);

    // Admin: поиск событий
    List<EventFullDto> getAdminEvents(List<Long> users, List<String> states, List<Long> categories,
                                     String rangeStart, String rangeEnd, int from, int size);

    // Admin: редактировать событие
    EventFullDto updateAdminEvent(long eventId, EventEditDto dto);

    // Public: поиск событий
    List<EventShortDto> getPublicEvents(String text, List<Long> categories, Boolean paid,
                                       String rangeStart, String rangeEnd,
                                       boolean onlyAvailable, String sort, int from, int size);

    // Public: получить событие по id
    EventFullDto getPublicEvent(long eventId);
}
