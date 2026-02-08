package ru.mrstepan.ewmservice.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.mrstepan.ewmservice.dto.UserDto;
import ru.mrstepan.ewmservice.service.UserService;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public Collection<UserDto> getUsers(
            @RequestParam(name = "from") int from,
            @RequestParam(name = "ids") List<Long> ids,
            @RequestParam(name = "size") int size
    ) {
        return userService.getUsers(ids, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void addUser(@RequestBody UserDto userDto) {
        userService.addUser(userDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteUser(@PathVariable long id) {
        userService.deleteUser(id);
    }
}
