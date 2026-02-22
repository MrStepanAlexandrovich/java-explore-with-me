package ru.mrstepan.ewmservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.mrstepan.ewmservice.dao.UserRepository;
import ru.mrstepan.ewmservice.dto.NewUserDto;
import ru.mrstepan.ewmservice.dto.UserDto;
import ru.mrstepan.ewmservice.exception.ConflictException;
import ru.mrstepan.ewmservice.exception.NotFoundException;
import ru.mrstepan.ewmservice.model.User;
import ru.mrstepan.ewmservice.mapper.UserMapper;
import ru.mrstepan.ewmservice.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<UserDto> getUsers(List<Long> ids, int from, int size) {
        log.info("Getting users. ids: {}, from: {}, size: {}", ids, from, size);
        List<UserDto> users;
        if (ids != null && !ids.isEmpty()) {
            users = userRepository.findAllByIdIn(ids).stream()
                    .map(UserMapper::toDto)
                    .collect(Collectors.toList());
        } else {
            users = userRepository.findAll(PageRequest.of(from / size, size)).stream()
                    .map(UserMapper::toDto)
                    .collect(Collectors.toList());
        }
        log.info("Found {} users", users.size());
        return users;
    }

    @Override
    public UserDto addUser(NewUserDto dto) {
        log.info("Adding user. Name: {}, email: {}", dto.getName(), dto.getEmail());
        try {
            User saved = userRepository.save(UserMapper.toUser(dto));
            log.info("User saved. Id: {}", saved.getId());
            return UserMapper.toDto(saved);
        } catch (DataIntegrityViolationException e) {
            log.error("User with email '{}' already exists", dto.getEmail());
            throw new ConflictException("User with email '" + dto.getEmail() + "' already exists");
        }
    }

    @Override
    public void deleteUser(long id) {
        log.info("Deleting user with id: {}", id);
        if (!userRepository.existsById(id)) {
            log.error("User with id={} was not found", id);
            throw new NotFoundException("User with id=" + id + " was not found");
        }
        userRepository.deleteById(id);
        log.info("User with id: {} deleted", id);
    }
}
