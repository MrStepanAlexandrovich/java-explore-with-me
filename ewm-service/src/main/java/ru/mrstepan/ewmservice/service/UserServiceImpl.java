package ru.mrstepan.ewmservice.service;

import org.springframework.stereotype.Service;
import ru.mrstepan.ewmservice.dto.UserDto;

import java.util.Collection;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Override
    public Collection<UserDto> getUsers(Collection<Long> ids, int from, int size) {
        return List.of();
    }

    @Override
    public void addUser(UserDto userDto) {

    }

    @Override
    public void deleteUser(long id) {

    }
}
