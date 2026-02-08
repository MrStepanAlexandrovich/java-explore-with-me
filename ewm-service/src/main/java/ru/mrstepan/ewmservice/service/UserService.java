package ru.mrstepan.ewmservice.service;

import ru.mrstepan.ewmservice.dto.UserDto;

import java.util.Collection;

public interface UserService {
    Collection<UserDto> getUsers(Collection<Long> ids, int from, int size);

    void addUser(UserDto userDto);

    void deleteUser(long id);
}
