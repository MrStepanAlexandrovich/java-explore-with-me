package ru.mrstepan.ewmservice.service;

import ru.mrstepan.ewmservice.dto.RequestDto;

import java.util.List;

public interface RequestService {
    List<RequestDto> getRequests(long userId);

    RequestDto addRequest(long userId, long eventId);

    RequestDto cancelRequest(long userId, long requestId);
}
