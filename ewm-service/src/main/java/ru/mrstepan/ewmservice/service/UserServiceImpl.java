package ru.mrstepan.ewmservice.service;

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
import ru.mrstepan.ewmservice.model.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<UserDto> getUsers(List<Long> ids, int from, int size) {
        if (ids != null && !ids.isEmpty()) {
            return userRepository.findAllByIdIn(ids).stream()
                    .map(UserMapper::toDto)
                    .collect(Collectors.toList());
        }
        return userRepository.findAll(PageRequest.of(from / size, size)).stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto addUser(NewUserDto dto) {
        try {
            User saved = userRepository.save(UserMapper.toUser(dto));
            return UserMapper.toDto(saved);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("User with email '" + dto.getEmail() + "' already exists");
        }
    }

    @Override
    public void deleteUser(long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("User with id=" + id + " was not found");
        }
        userRepository.deleteById(id);
    }
}
