package ru.practicum.shareit.booking;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    @Autowired
    ObjectMapper mapper;

    @MockBean
    BookingClient bookingClient;

    @Autowired
    private MockMvc mvc;

    UserDto bookerDto;
    UserDto ownerDto;
    ItemDto itemDto;

    private BookingRequestDto bookingRequestDto;
    private BookingResponseDto bookingResponseDto;

    @BeforeEach
    void setUp() {
        LocalDateTime localDateTime = LocalDateTime.now();

        bookerDto = UserDto.builder()
                .name("John Doe")
                .email("johndoe@gmail.com")
                .id(1L)
                .build();

        ownerDto = UserDto.builder()
                .name("Zima Blue")
                .email("zimablue@gmail.com")
                .id(2L)
                .build();

        itemDto = ItemDto.builder()
                .id(1L)
                .available(true)
                .userId(ownerDto.getId())
                .description("test description for item")
                .name("Shovel")
                .build();

        bookingRequestDto = BookingRequestDto.builder()
                .itemId(1L)
                .start(localDateTime.plusDays(2))
                .end(localDateTime.plusDays(4))
                .bookerId(2L)
                .build();

        bookingResponseDto = BookingResponseDto.builder()
                .id(1L)
                .start(bookingRequestDto.getStart())
                .end(bookingRequestDto.getEnd())
                .status("WAITING")
                .booker(bookerDto)
                .item(itemDto)
                .build();
    }

    @Test
    void bookItem() throws Exception {
        ResponseEntity<Object> responseEntity = ResponseEntity.ok(mapper.writeValueAsString(bookingResponseDto));

        Mockito
                .when(bookingClient.bookItem(bookerDto.getId(), bookingRequestDto))
                .thenReturn(responseEntity);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", String.valueOf(bookerDto.getId()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.item.name").value("Shovel"));

    }

    @Test
    void acceptOrDeclineBooking() throws Exception {
        bookingResponseDto.setStatus("APPROVED");
        ResponseEntity<Object> responseEntity = ResponseEntity.ok(mapper.writeValueAsString(bookingResponseDto));

        Mockito
                .when(bookingClient.acceptOrDeclineBooking(ownerDto.getId(), 1L, true))
                .thenReturn(responseEntity);

        mvc.perform(patch("/bookings/{bookingId}", bookingResponseDto.getId())
                        .header("X-Sharer-User-Id", ownerDto.getId())
                        .param("approved", String.valueOf(true)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void getOneBooking() throws Exception {
        ResponseEntity<Object> responseEntity = ResponseEntity.ok(mapper.writeValueAsString(bookingResponseDto));

        Mockito
                .when(bookingClient.getBooking(bookerDto.getId(), bookingResponseDto.getId()))
                .thenReturn(responseEntity);

        mvc.perform(get("/bookings/{bookingId}", bookingResponseDto.getId())
                        .header("X-Sharer-User-Id", bookerDto.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.item.name").value("Shovel"));
    }

    @Test
    void getAllUserBookings() throws Exception {
        List<BookingResponseDto> expectedList;

        BookingResponseDto newBooking = BookingResponseDto.builder()
                .id(2L)
                .start(bookingRequestDto.getStart())
                .end(bookingRequestDto.getEnd())
                .status("APPROVED")
                .booker(bookerDto)
                .item(itemDto)
                .build();

        expectedList = List.of(bookingResponseDto, newBooking);

        ResponseEntity<Object> responseEntity = ResponseEntity.ok(mapper.writeValueAsString(expectedList));

        Mockito
                .when(bookingClient.getUserBookings(bookerDto.getId(), "ALL", 1, 10, false))
                .thenReturn(responseEntity);

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", bookerDto.getId())
                        .param("state", "ALL")
                        .param("from", String.valueOf(1))
                        .param("size", String.valueOf(10)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(expectedList.get(0).getId()))
                .andExpect(jsonPath("$[1].id").value(expectedList.get(1).getId()));
    }

    @Test
    void getAllOwnerBookings() throws Exception {
        List<BookingResponseDto> expectedList;

        BookingResponseDto newBooking = BookingResponseDto.builder()
                .id(2L)
                .start(bookingRequestDto.getStart())
                .end(bookingRequestDto.getEnd())
                .status("APPROVED")
                .booker(bookerDto)
                .item(itemDto)
                .build();

        expectedList = List.of(bookingResponseDto, newBooking);

        ResponseEntity<Object> responseEntity = ResponseEntity.ok(mapper.writeValueAsString(expectedList));

        Mockito
                .when(bookingClient.getUserBookings(bookerDto.getId(), "ALL", 1, 10, true))
                .thenReturn(responseEntity);

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", bookerDto.getId())
                        .param("state", "ALL")
                        .param("from", String.valueOf(1))
                        .param("size", String.valueOf(10)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(expectedList.get(0).getId()))
                .andExpect(jsonPath("$[1].id").value(expectedList.get(1).getId()));
    }
}