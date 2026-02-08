package ru.mrstepan.ewmservice.service;

import ru.mrstepan.ewmservice.dto.RequestDto;

import java.util.Collection;
import java.util.List;

public class RequestServiceImpl implements RequestService {
    @Override
    public Collection<RequestDto> getRequests(long userId) {
        return List.of();
    }

    @Override
    public void addRequest(long userId, long eventId) {

    }

    @Override
    public void cancelRequest(long userId, long requestId) {

    }
}
