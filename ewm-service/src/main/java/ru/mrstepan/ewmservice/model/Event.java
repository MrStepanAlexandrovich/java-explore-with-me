package ru.mrstepan.ewmservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class Event {
    @Id
    private long id;
    private String annotation; //краткое описание
    private Category category;
    private String description; //полное описание
    private LocalDateTime eventDate;
    private Location location;
    private boolean paid;
    private Integer participantLimit;
    private boolean requestModeration;
    private String title;
}
