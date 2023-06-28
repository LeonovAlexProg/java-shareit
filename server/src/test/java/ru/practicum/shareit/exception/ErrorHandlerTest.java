package ru.practicum.shareit.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.exceptions.AcceptBookingException;
import ru.practicum.shareit.booking.exceptions.BookingNotFoundException;
import ru.practicum.shareit.booking.exceptions.BookingValidationException;
import ru.practicum.shareit.booking.exceptions.ItemBookingException;
import ru.practicum.shareit.item.exceptions.CommentValidationException;
import ru.practicum.shareit.item.exceptions.ItemAccessRestrictedException;
import ru.practicum.shareit.item.exceptions.ItemExistsException;
import ru.practicum.shareit.item.exceptions.ItemNotFoundException;
import ru.practicum.shareit.request.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.request.exception.PaginationDataException;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exceptions.EmailExistsException;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.service.UserService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {UserController.class})
class ErrorHandlerTest {
    @Autowired
    ObjectMapper mapper;
    @Autowired
    MockMvc mvc;
    @MockBean
    UserService userService;

    @Test
    void emailExistsHandler() throws Exception {
        UserDto request = UserDto.builder()
                .name("alex")
                .email("test@mail.ru")
                .build();

        Mockito
                .when(userService.createUser(request))
                .thenThrow(new EmailExistsException("Email is already taken"));

        mvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof EmailExistsException))
                .andExpect(result -> Assertions.assertEquals("Email is already taken", result.getResolvedException().getMessage()));
    }

    @Test
    void userNotFound() throws Exception {
        UserDto request = UserDto.builder()
                .name("alex")
                .email("test@mail.ru")
                .build();

        Mockito
                .when(userService.createUser(request))
                .thenThrow(new UserNotFoundException("User id %d not found"));

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof UserNotFoundException))
                .andExpect(result -> Assertions.assertEquals("User id %d not found", result.getResolvedException().getMessage()));
    }

    @Test
    void itemExistsHandler() throws Exception {
        UserDto request = UserDto.builder()
                .name("alex")
                .email("test@mail.ru")
                .build();

        Mockito
                .when(userService.createUser(request))
                .thenThrow(new ItemExistsException("Item id %d exists"));

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof ItemExistsException))
                .andExpect(result -> Assertions.assertEquals("Item id %d exists", result.getResolvedException().getMessage()));
    }

    @Test
    void accessRestrictedHandler() throws Exception {
        UserDto request = UserDto.builder()
                .name("alex")
                .email("test@mail.ru")
                .build();

        Mockito
                .when(userService.createUser(request))
                .thenThrow(new ItemAccessRestrictedException("Access restricted"));

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof ItemAccessRestrictedException))
                .andExpect(result -> Assertions.assertEquals("Access restricted", result.getResolvedException().getMessage()));
    }

    @Test
    void itemNotFoundHandler() throws Exception {
        UserDto request = UserDto.builder()
                .name("alex")
                .email("test@mail.ru")
                .build();

        Mockito
                .when(userService.createUser(request))
                .thenThrow(new ItemNotFoundException("Item not found"));

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof ItemNotFoundException))
                .andExpect(result -> Assertions.assertEquals("Item not found", result.getResolvedException().getMessage()));
    }

    @Test
    void bookingValidationHandler() throws Exception {
        UserDto request = UserDto.builder()
                .name("alex")
                .email("test@mail.ru")
                .build();

        Mockito
                .when(userService.createUser(request))
                .thenThrow(new BookingValidationException("Validation exception"));

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof BookingValidationException))
                .andExpect(result -> Assertions.assertEquals("Validation exception", result.getResolvedException().getMessage()));
    }

    @Test
    void itemBookingHandler() throws Exception {
        UserDto request = UserDto.builder()
                .name("alex")
                .email("test@mail.ru")
                .build();

        Mockito
                .when(userService.createUser(request))
                .thenThrow(new ItemBookingException("Item booking exception"));

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof ItemBookingException))
                .andExpect(result -> Assertions.assertEquals("Item booking exception", result.getResolvedException().getMessage()));
    }

    @Test
    void bookingNotFoundHandler() throws Exception {
        UserDto request = UserDto.builder()
                .name("alex")
                .email("test@mail.ru")
                .build();

        Mockito
                .when(userService.createUser(request))
                .thenThrow(new BookingNotFoundException("Booking not found"));

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof BookingNotFoundException))
                .andExpect(result -> Assertions.assertEquals("Booking not found", result.getResolvedException().getMessage()));
    }

    @Test
    void acceptBookingHandler() throws Exception {
        UserDto request = UserDto.builder()
                .name("alex")
                .email("test@mail.ru")
                .build();

        Mockito
                .when(userService.createUser(request))
                .thenThrow(new AcceptBookingException("Accept booking exception"));

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof AcceptBookingException))
                .andExpect(result -> Assertions.assertEquals("Accept booking exception", result.getResolvedException().getMessage()));
    }

    @Test
    void commentValidationHandler() throws Exception {
        UserDto request = UserDto.builder()
                .name("alex")
                .email("test@mail.ru")
                .build();

        Mockito
                .when(userService.createUser(request))
                .thenThrow(new CommentValidationException("Comment validation exception"));

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof CommentValidationException))
                .andExpect(result -> Assertions.assertEquals("Comment validation exception", result.getResolvedException().getMessage()));
    }

    @Test
    void paginationDataHandler() throws Exception {
        UserDto request = UserDto.builder()
                .name("alex")
                .email("test@mail.ru")
                .build();

        Mockito
                .when(userService.createUser(request))
                .thenThrow(new PaginationDataException("Pagination exception"));

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof PaginationDataException))
                .andExpect(result -> Assertions.assertEquals("Pagination exception", result.getResolvedException().getMessage()));
    }

    @Test
    void itemRequestNotFoundHandler() throws Exception {
        UserDto request = UserDto.builder()
                .name("alex")
                .email("test@mail.ru")
                .build();

        Mockito
                .when(userService.createUser(request))
                .thenThrow(new ItemRequestNotFoundException("Item request not found"));

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof ItemRequestNotFoundException))
                .andExpect(result -> Assertions.assertEquals("Item request not found", result.getResolvedException().getMessage()));
    }
}