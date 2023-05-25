package ru.practicum.shareit.user.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserMapper {
//    public User map(UserDto userDto) {
//        return User.builder()
//                .email(userDto.getEmail())
//                .name(userDto.getName())
//                .build();
//    }
//
//    public User map(int userId, UserDto userDto) {
//        return User.builder()
//                .id(userId)
//                .email(userDto.getEmail())
//                .name(userDto.getName())
//                .build();
//    }
//
//    public UserDto map(User user) {
//        return UserDto.builder()
//                .id(user.getId())
//                .email(user.getEmail())
//                .name(user.getName())
//                .build();
//    }
//
//    public List<UserDto> map(List<User> users) {
//        return users.stream().map(this::map).collect(Collectors.toList());
//    }
}
