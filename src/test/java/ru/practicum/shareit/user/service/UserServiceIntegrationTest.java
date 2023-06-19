package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserServiceIntegrationTest {
    private final UserService userService;
    private final UserRepository userRepository;

    User user;
    UserDto userDto;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .name("zima")
                .email("zimeblue@gmail.com")
                .build();

        userDto = UserDto.builder()
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    @Test
    void createUser() {
        UserDto expectedDto;
        UserDto actualDto;

        UserDto userSaved = userService.createUser(userDto);

        Optional<User> persistedUser = userRepository.findById(userSaved.getId());

        Assertions.assertTrue(persistedUser.isPresent());

        expectedDto = userSaved;
        actualDto = UserMapper.userDtoOf(persistedUser.get());

        Assertions.assertEquals(expectedDto, actualDto);
    }

    @Test
    void getAllUsers() {
        List<UserDto> expectedList;
        List<UserDto> actualList;

        User userTwo = User.builder()
                .name("alex")
                .email("alex@mail.ru")
                .build();
        User userThree = User.builder()
                .name("john")
                .email("john@mail.ru")
                .build();

        User userSavedOne = userRepository.save(user);
        User userSavedTwo = userRepository.save(userTwo);
        User userSavedThree = userRepository.save(userThree);

        expectedList = UserMapper.userDtoListOf(List.of(userSavedOne, userSavedTwo, userSavedThree));
        actualList = userService.getAllUsers();

        Assertions.assertEquals(expectedList, actualList);
    }

    @Test
    void getUser() {
        UserDto expectedDto;
        UserDto actualDto;

        User userSaved = userRepository.save(user);

        expectedDto = UserMapper.userDtoOf(userSaved);
        actualDto = userService.getUser(userSaved.getId());

        Assertions.assertEquals(expectedDto, actualDto);
    }

    @Test
    void patchUser() {
        UserDto expectedDto;
        UserDto actualDto;

        User userThree = User.builder()
                .name("john")
                .email("john@mail.ru")
                .build();
        User userSavedThree = userRepository.save(userThree);

        userDto.setId(1L);
        expectedDto = userDto;
        actualDto = userService.patchUser(userDto);

        Assertions.assertEquals(expectedDto, actualDto);
    }

    @Test
    void deleteUser() {
        UserDto expectedDto;
        UserDto actualDto;

        User userSaved = userRepository.save(user);

        expectedDto = UserMapper.userDtoOf(userSaved);
        actualDto = userService.getUser(userSaved.getId());

        Assertions.assertEquals(expectedDto, actualDto);

        userService.deleteUser(userSaved.getId());

        Assertions.assertFalse(userRepository.existsById(userSaved.getId()));
    }

}