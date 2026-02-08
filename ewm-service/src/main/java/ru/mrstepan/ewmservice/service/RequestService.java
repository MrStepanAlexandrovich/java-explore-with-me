package ru.mrstepan.ewmservice.service;

import ru.mrstepan.ewmservice.dto.RequestDto;

import java.util.Collection;

public interface RequestService {
    Collection<RequestDto> getRequests(long userId);

    void addRequest(long userId, long eventId);

    void cancelRequest(long userId, long requestId);
}
