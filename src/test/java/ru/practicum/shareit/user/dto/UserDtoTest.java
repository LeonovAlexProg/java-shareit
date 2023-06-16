package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.model.User;

import java.util.List;

class UserDtoTest {
    User user = User.builder()
            .id(1L)
            .name("alex")
            .email("alex@mail.ru")
            .build();

    @Test
    void of() {
        UserDto expectedDto;
        UserDto actualDto;

        expectedDto = UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
        actualDto = UserDto.of(user);

        Assertions.assertEquals(expectedDto, actualDto);
    }

    @Test
    void listOf() {
        List<UserDto> expectedList;
        List<UserDto> actualList;

        UserDto correctDto = UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();

        expectedList = List.of(correctDto, correctDto, correctDto);
        actualList = UserDto.listOf(List.of(user, user, user));

        Assertions.assertEquals(expectedList, actualList);
    }
}