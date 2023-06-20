package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.constraints.UserCreateConstraint;
import ru.practicum.shareit.user.constraints.UserIdConstraint;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping()
    public UserDto createNewUser(@RequestBody UserDto userDto) {
        UserDto createdUser = userService.createUser(userDto);
        log.info("User created id {} name {} email {}", createdUser.getId(), createdUser.getName(), createdUser.getEmail());
        return createdUser;
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable long userId) {
        return userService.getUser(userId);
    }

    @PatchMapping("/{userId}")
    public UserDto patchUser(@PathVariable long userId,
                          @Validated(UserIdConstraint.class) @Valid @RequestBody UserDto userDto) {
        userDto.setId(userId);
        return userService.patchUser(userDto);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable long userId) {
        userService.deleteUser(userId);
    }
}
