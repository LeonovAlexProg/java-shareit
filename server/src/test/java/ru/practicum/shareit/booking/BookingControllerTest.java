package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

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
    BookingService bookingService;

    @Autowired
    private MockMvc mvc;

    private User owner;
    private User booker;
    private BookingRequestDto bookingRequestDto;
    private BookingResponseDto bookingResponseDto;
    private Item item;

    @BeforeEach
    void setUp() {
        LocalDateTime localDateTime = LocalDateTime.now();

        owner = User.builder()
                .id(1L)
                .name("zima")
                .email("blue")
                .build();

        booker = User.builder()
                .id(2L)
                .name("adam")
                .email("smasher")
                .build();

        item = Item.builder()
                .id(1L)
                .name("drill")
                .description("for drilling")
                .user(owner)
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
                .booker(UserMapper.userDtoOf(booker))
                .item(ItemMapper.itemDtoOf(item))
                .build();
    }

    @Test
    void bookNewItem() throws Exception {
        Mockito
                .when(bookingService.bookItem(bookingRequestDto))
                .thenReturn(bookingResponseDto);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", String.valueOf(booker.getId()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.item.name").value("drill"));
    }

    @Test
    void acceptOrDeclineBooking() throws Exception {
        BookingResponseDto approvedBooking = BookingResponseDto.builder()
                .id(1L)
                .start(bookingRequestDto.getStart())
                .end(bookingRequestDto.getEnd())
                .status("APPROVED")
                .booker(UserMapper.userDtoOf(booker))
                .item(ItemMapper.itemDtoOf(item))
                .build();

        Mockito
                .when(bookingService.acceptOrDeclineBooking(owner.getId(), bookingResponseDto.getId(), true))
                .thenReturn(approvedBooking);

        mvc.perform(patch("/bookings/{bookingId}", bookingResponseDto.getId())
                        .header("X-Sharer-User-Id", owner.getId())
                        .param("approved", String.valueOf(true)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void getOneBooking() throws Exception {
        Mockito
                .when(bookingService.getBooking(bookingResponseDto.getBooker().getId(), bookingResponseDto.getId()))
                .thenReturn(bookingResponseDto);

        mvc.perform(get("/bookings/{bookingId}", bookingResponseDto.getId())
                        .header("X-Sharer-User-Id", booker.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingResponseDto.getId()))
                .andExpect(jsonPath("$.status").value(bookingResponseDto.getStatus()));
    }

    @Test
    void getAllUserBookings() throws Exception {
        List<BookingResponseDto> expectedList;

        BookingResponseDto newBooking = BookingResponseDto.builder()
                .id(2L)
                .start(bookingRequestDto.getStart())
                .end(bookingRequestDto.getEnd())
                .status("APPROVED")
                .booker(UserMapper.userDtoOf(booker))
                .item(ItemMapper.itemDtoOf(item))
                .build();

        expectedList = List.of(bookingResponseDto, newBooking);

        Mockito
                .when(bookingService.getUserBookings(booker.getId(), "ALL", false, 0, 10))
                .thenReturn(expectedList);

        mvc.perform(get("/bookings")
                .header("X-Sharer-User-Id", booker.getId())
                .param("state", "ALL")
                .param("from", String.valueOf(0))
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
                .booker(UserMapper.userDtoOf(booker))
                .item(ItemMapper.itemDtoOf(item))
                .build();

        expectedList = List.of(bookingResponseDto, newBooking);

        Mockito
                .when(bookingService.getUserBookings(booker.getId(), "ALL", true, 0, 10))
                .thenReturn(expectedList);

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", booker.getId())
                        .param("state", "ALL")
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(10)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(expectedList.get(0).getId()))
                .andExpect(jsonPath("$[1].id").value(expectedList.get(1).getId()));
    }
}