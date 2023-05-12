package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.constraints.UserCreateConstraint;
import ru.practicum.shareit.user.constraints.UserIdConstraint;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserMapper mapper;

    @GetMapping
    public List<UserDto> getAllUsers() {
        return mapper.map(userService.getAllUsers());
    }

    @PostMapping()
    public UserDto createNewUser(@Validated({UserIdConstraint.class, UserCreateConstraint.class}) @RequestBody UserDto userDto) {
        User user = mapper.map(userDto);
        return mapper.map(userService.createUser(user));
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable int userId) {
        return mapper.map(userService.getUser(userId));
    }

    @PatchMapping("/{userId}")
    public UserDto patchUser(@PathVariable int userId,
                          @Validated(UserIdConstraint.class) @Valid @RequestBody UserDto userDto) {
        User user = mapper.map(userId, userDto);
        return mapper.map(userService.patchUser(user));
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable int userId) {
        userService.deleteUser(userId);
    }
}
