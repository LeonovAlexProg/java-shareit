package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.constraints.UserCreateConstraint;
import ru.practicum.shareit.user.constraints.UserIdConstraint;
import ru.practicum.shareit.user.dto.UserDto;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    @PostMapping()
    public ResponseEntity<Object> createNewUser(@Validated({UserIdConstraint.class, UserCreateConstraint.class}) @RequestBody UserDto userDto) {
        log.info("Create new user name {} email {}", userDto.getName(), userDto.getEmail());
        return userClient.newUser(userDto);
    }
}
