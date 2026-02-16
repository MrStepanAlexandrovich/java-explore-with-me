package ru.mrstepan.ewmservice.service;

import ru.mrstepan.ewmservice.dto.NewUserRequest;
import ru.mrstepan.ewmservice.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getUsers(List<Long> ids, int from, int size);

    UserDto addUser(NewUserRequest dto);

    void deleteUser(long id);
}
