package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers();

    UserDto createUser(UserDto userDto);

    UserDto getUser(Long userId);

    UserDto patchUser(UserDto userDto);

    void deleteUser(Long userId);
}
