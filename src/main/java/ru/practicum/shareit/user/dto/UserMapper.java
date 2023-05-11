package ru.practicum.shareit.user.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

@Component
public class UserMapper {
    public User map(UserDto userDto) {
        return User.builder()
                .email(userDto.getEmail())
                .name(userDto.getName())
                .build();
    }

    public User map(int userId, UserDto userDto) {
        return User.builder()
                .id(userId)
                .email(userDto.getEmail())
                .name(userDto.getName())
                .build();
    }
}
