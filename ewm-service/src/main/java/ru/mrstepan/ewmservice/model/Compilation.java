package ru.mrstepan.ewmservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.util.List;

@Entity
public class Compilation {
    @Id
    private long id;
    private String title;
    private boolean pinned;
    private List<Event> events;
}
