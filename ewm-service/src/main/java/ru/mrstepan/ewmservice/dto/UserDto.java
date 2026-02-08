package ru.mrstepan.ewmservice.dto;

import lombok.Data;

@Data
public class UserDto {
    private long id;
    private String name;
    @Email
    private String email;
}
