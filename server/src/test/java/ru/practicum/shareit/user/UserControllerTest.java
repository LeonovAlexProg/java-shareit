package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {
    @Autowired
    ObjectMapper mapper;

    @MockBean
    UserService userService;

    @Autowired
    MockMvc mvc;

    UserDto userRequestDto;
    UserDto userResponseDto;

    @BeforeEach
    void setUp() {
        userRequestDto = UserDto.builder()
                .name("zima")
                .email("zimablue@gmail.com")
                .build();

        userResponseDto = UserDto.builder()
                .id(1L)
                .name(userRequestDto.getName())
                .email(userRequestDto.getEmail())
                .build();
    }

    @Test
    void createNewUser() throws Exception {
        Mockito
                .when(userService.createUser(userRequestDto))
                .thenReturn(userResponseDto);

        mvc.perform(post("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userResponseDto.getId()))
                .andExpect(jsonPath("$.name").value(userResponseDto.getName()))
                .andExpect(jsonPath("$.email").value(userResponseDto.getEmail()));
    }

    @Test
    void getAllUsers() throws Exception {
        List<UserDto> expectedList;

        UserDto newUserResponseDto = UserDto.builder()
                .id(2L)
                .name("adam")
                .email("adamsandler@mail.com")
                .build();

        expectedList = List.of(userResponseDto, newUserResponseDto);

        Mockito
                .when(userService.getAllUsers())
                .thenReturn(expectedList);

        mvc.perform(get("/users"))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(userResponseDto.getId()))
                .andExpect(jsonPath("$[1].id").value(newUserResponseDto.getId()));
    }

    @Test
    void getUser() throws Exception {
        Mockito
                .when(userService.getUser(userResponseDto.getId()))
                .thenReturn(userResponseDto);

        mvc.perform(get("/users/{userId}", userResponseDto.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userResponseDto.getId()))
                .andExpect(jsonPath("$.name").value(userResponseDto.getName()))
                .andExpect(jsonPath("$.email").value(userResponseDto.getEmail()));
    }

    @Test
    void patchUser() throws Exception {
        Mockito
                .when(userService.patchUser(userResponseDto))
                .thenReturn(userResponseDto);

        mvc.perform(patch("/users/{userId}", userResponseDto.getId())
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(userRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userResponseDto.getId()))
                .andExpect(jsonPath("$.name").value(userResponseDto.getName()))
                .andExpect(jsonPath("$.email").value(userResponseDto.getEmail()));
    }

    @Test
    void deleteUser() throws Exception {
        mvc.perform(delete("/users/{userId}", userResponseDto.getId()))
                .andExpect(status().isOk());

        Mockito
                .verify(userService, Mockito.times(1))
                .deleteUser(userResponseDto.getId());
    }
}