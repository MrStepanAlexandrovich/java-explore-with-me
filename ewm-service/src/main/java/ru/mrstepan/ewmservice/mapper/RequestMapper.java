package ru.mrstepan.ewmservice.mapper;

import ru.mrstepan.ewmservice.dto.RequestDto;
import ru.mrstepan.ewmservice.model.Request;

public class RequestMapper {
    public static RequestDto toDto(Request request) {
        return new RequestDto(
                request.getCreated() != null ? request.getCreated() : null,
                request.getEvent() != null ? request.getEvent().getId() : null,
                request.getId(),
                request.getRequester() != null ? request.getRequester().getId() : null,
                request.getStatus() != null ? request.getStatus().name() : null
        );
    }
}
