package ru.mrstepan.ewmservice.model;

import lombok.Data;

@Data
public class User {
    private long id;
    private String name;
    private String email;
}
