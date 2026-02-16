package ru.mrstepan.ewmservice.model;

import ru.mrstepan.ewmservice.dto.NewUserDto;
import ru.mrstepan.ewmservice.dto.UserDto;
import ru.mrstepan.ewmservice.dto.UserShortDto;

public class UserMapper {
    public static User toUser(NewUserDto dto) {
        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        return user;
    }

    public static UserDto toDto(User user) {
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }

    public static UserShortDto toShortDto(User user) {
        return new UserShortDto(user.getId(), user.getName());
    }
}
