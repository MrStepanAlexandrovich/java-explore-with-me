package ru.mrstepan.ewmservice.dto;

import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.mrstepan.ewmservice.model.Location;

import java.time.LocalDateTime;

@Data
public class EventEditDto {
    @Size(min = 20, max = 2000)
    private String annotation;

    private Long category;

    @Size(min = 20, max = 7000)
    private String description;

    private LocalDateTime eventDate;
    private Location location;
    private Boolean paid;

    @PositiveOrZero
    private Integer participantLimit;
    private Boolean requestModeration;
    private String stateAction;

    @Size(min = 3, max = 120)
    private String title;
}
