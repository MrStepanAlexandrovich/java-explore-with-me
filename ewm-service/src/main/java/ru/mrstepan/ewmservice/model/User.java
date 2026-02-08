package ru.mrstepan.ewmservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class User {
    @Id
    private long id;
    private String name;
    private String email;
}
