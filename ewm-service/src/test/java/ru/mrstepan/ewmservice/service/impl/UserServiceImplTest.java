package ru.mrstepan.ewmservice.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.mrstepan.ewmservice.dao.UserRepository;
import ru.mrstepan.ewmservice.dto.NewUserDto;
import ru.mrstepan.ewmservice.dto.UserDto;
import ru.mrstepan.ewmservice.exception.ConflictException;
import ru.mrstepan.ewmservice.exception.NotFoundException;
import ru.mrstepan.ewmservice.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private NewUserDto newUserDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@test.com");

        newUserDto = new NewUserDto();
        newUserDto.setName("Test User");
        newUserDto.setEmail("test@test.com");
    }

    @Test
    void getUsers_WithIds_Success() {
        List<Long> ids = List.of(1L, 2L);
        List<User> users = List.of(user);

        when(userRepository.findAllByIdIn(ids)).thenReturn(users);

        List<UserDto> result = userService.getUsers(ids, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test User", result.get(0).getName());
    }

    @Test
    void getUsers_WithoutIds_Success() {
        Page<User> userPage = new PageImpl<>(List.of(user));

        when(userRepository.findAll(any(PageRequest.class))).thenReturn(userPage);

        List<UserDto> result = userService.getUsers(null, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getUsers_EmptyIds_Success() {
        Page<User> userPage = new PageImpl<>(List.of(user));

        when(userRepository.findAll(any(PageRequest.class))).thenReturn(userPage);

        List<UserDto> result = userService.getUsers(List.of(), 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void addUser_Success() {
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto result = userService.addUser(newUserDto);

        assertNotNull(result);
        assertEquals("Test User", result.getName());
        assertEquals("test@test.com", result.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void addUser_DuplicateEmail_ThrowsConflict() {
        when(userRepository.save(any(User.class)))
                .thenThrow(new DataIntegrityViolationException("Duplicate"));

        assertThrows(ConflictException.class, () ->
                userService.addUser(newUserDto));
    }

    @Test
    void deleteUser_Success() {
        when(userRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> userService.deleteUser(1L));

        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUser_NotFound() {
        when(userRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(NotFoundException.class, () ->
                userService.deleteUser(999L));

        verify(userRepository, never()).deleteById(anyLong());
    }
}
