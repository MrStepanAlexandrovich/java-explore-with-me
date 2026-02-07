package ru.mrstepan.ewmservice.controller.admin;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/users")
public class UserController {
    @GetMapping
    public Collection<UserDto> getUsers(
            @RequestParam(name = "from") int from,
            @RequestParam(name = "ids") List<Integer> users,
            @RequestParam(name = "size") int size
    ) {

    }

    @PostMapping
    public void addUser(@RequestBody UserDto userDto) {

    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable long id) {

    }
}
