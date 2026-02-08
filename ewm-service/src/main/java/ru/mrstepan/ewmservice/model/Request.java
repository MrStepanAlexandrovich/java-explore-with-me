package ru.mrstepan.ewmservice.model;

import jakarta.persistence.Entity;

import java.time.LocalDateTime;

@Entity
public class Request {
    private long id;
    private LocalDateTime created;
    private Event event;
    private User requester;
    private Status status;
}
