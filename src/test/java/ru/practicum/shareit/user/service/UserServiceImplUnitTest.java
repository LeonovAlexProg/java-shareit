package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UserServiceImplUnitTest {
    @InjectMocks
    UserServiceImpl userService;

    @Mock
    UserRepository userRepository;

    UserDto firstUserDto;
    UserDto secondUserDto;
    UserDto thirdUserDto;
    UserDto firstUserDtoSaved;
    UserDto secondUserDtoSaved;
    UserDto thirdUserDtoSaved;
    User firstUser;
    User secondUser;
    User thirdUser;
    User firstUserSaved;
    User secondUserSaved;
    User thirdUserSaved;

    @BeforeEach
    void setUp() {
        firstUserDto = UserDto.builder()
                .name("zima")
                .email("zimablue@gmail.com")
                .build();
        secondUserDto = UserDto.builder()
                .name("alex")
                .email("alex@gmail.com")
                .build();
        thirdUserDto = UserDto.builder()
                .name("dima")
                .email("dima@gmail.com")
                .build();

        firstUserDtoSaved = UserDto.builder()
                .id(1L)
                .name("zima")
                .email("zimablue@gmail.com")
                .build();
        secondUserDtoSaved = UserDto.builder()
                .id(2L)
                .name("alex")
                .email("alex@gmail.com")
                .build();
        thirdUserDtoSaved = UserDto.builder()
                .id(3L)
                .name("dima")
                .email("dima@gmail.com")
                .build();

        firstUser = User.builder()
                .name(firstUserDtoSaved.getName())
                .email(firstUserDtoSaved.getEmail())
                .build();
        secondUser = User.builder()
                .name(secondUserDtoSaved.getName())
                .email(secondUserDtoSaved.getEmail())
                .build();
        thirdUser = User.builder()
                .name(thirdUserDtoSaved.getName())
                .email(thirdUserDtoSaved.getEmail())
                .build();

        firstUserSaved = User.builder()
                .id(firstUserDtoSaved.getId())
                .name(firstUserDtoSaved.getName())
                .email(firstUserDtoSaved.getEmail())
                .build();
        secondUserSaved = User.builder()
                .id(secondUserDtoSaved.getId())
                .name(secondUserDtoSaved.getName())
                .email(secondUserDtoSaved.getEmail())
                .build();
        thirdUserSaved = User.builder()
                .id(thirdUserDtoSaved.getId())
                .name(thirdUserDtoSaved.getName())
                .email(thirdUserDtoSaved.getEmail())
                .build();
    }

    @Test
    void createUser() {
        UserDto expectedDto;
        UserDto actualDto;

        Mockito
                .when(userRepository.save(firstUser))
                .thenReturn(firstUserSaved);

        expectedDto = firstUserDtoSaved;
        actualDto = userService.createUser(firstUserDto);

        Assertions.assertEquals(expectedDto, actualDto);
    }

    @Test
    void getAllUsers() {
        List<UserDto> expectedList;
        List<UserDto> actualList;

        Mockito
                .when(userRepository.findAll())
                .thenReturn(List.of(firstUserSaved, secondUserSaved, thirdUserSaved));

        expectedList = List.of(firstUserDtoSaved, secondUserDtoSaved, thirdUserDtoSaved);
        actualList = userService.getAllUsers();

        Assertions.assertEquals(expectedList, actualList);
    }

    @Test
    void getUser() {
        UserDto expectedDto;
        UserDto actualDto;

        Mockito
                .when(userRepository.findById(firstUserSaved.getId()))
                .thenReturn(Optional.ofNullable(firstUserSaved));

        expectedDto = firstUserDtoSaved;
        actualDto = userService.getUser(firstUserDtoSaved.getId());

        Assertions.assertEquals(expectedDto, actualDto);
    }

    @Test
    void patchUser() {
        UserDto expectedDto;
        UserDto actualDto;

        UserDto patchedUserDto = UserDto.builder()
                .id(firstUserSaved.getId())
                .name(secondUserSaved.getName())
                .email(secondUserSaved.getEmail())
                .build();
        User patchedUser = User.builder()
                .id(firstUserSaved.getId())
                .name(secondUserSaved.getName())
                .email(secondUserSaved.getEmail())
                .build();
        secondUserDtoSaved.setId(1L);

        Mockito
                .when(userRepository.findById(firstUserDtoSaved.getId()))
                .thenReturn(Optional.of(firstUserSaved));
        Mockito
                .when(userRepository.save(patchedUser))
                .thenReturn(patchedUser);

        expectedDto = patchedUserDto;
        actualDto = userService.patchUser(secondUserDtoSaved);

        Assertions.assertEquals(expectedDto, actualDto);
    }

    @Test
    void deleteUser() {
        userService.deleteUser(firstUserDtoSaved.getId());

        Mockito.verify(userRepository, Mockito.times(1))
                .deleteById(firstUserDtoSaved.getId());
    }
}