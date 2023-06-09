package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

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
        log.info("Creating user id {} name {} email {}", createdUser.getId(), createdUser.getName(), createdUser.getEmail());
        return createdUser;
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.info("Getting all users");
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable long userId) {
        log.info("Getting user id {}", userId);
        return userService.getUser(userId);
    }

    @PatchMapping("/{userId}")
    public UserDto patchUser(@PathVariable long userId,
                          @RequestBody UserDto userDto) {
        log.info("Patching user id {}", userId);
        userDto.setId(userId);
        return userService.patchUser(userDto);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable long userId) {
        log.info("Deleting user id {}", userId);
        userService.deleteUser(userId);
    }
}
