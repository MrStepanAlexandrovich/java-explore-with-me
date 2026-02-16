package ru.mrstepan.ewmservice.model;

import ru.mrstepan.ewmservice.dto.RequestDto;

import java.time.format.DateTimeFormatter;

public class RequestMapper {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

    public static RequestDto toDto(Request request) {
        return new RequestDto(
                request.getCreated() != null ? request.getCreated().format(FORMATTER) : null,
                request.getEvent() != null ? request.getEvent().getId() : null,
                request.getId(),
                request.getRequester() != null ? request.getRequester().getId() : null,
                request.getStatus() != null ? request.getStatus().name() : null
        );
    }
}
