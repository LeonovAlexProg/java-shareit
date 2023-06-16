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
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    private LocalDateTime localDateTime;

    @BeforeEach
    void setUp() {
        localDateTime = LocalDateTime.now();

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
                .booker(UserDto.of(booker))
                .item(ItemDto.of(item))
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
    void acceptOrDeclineBooking() {
        bookingResponseDto.setStatus("APPROVED");

        Mockito
                .when(bookingService.acceptOrDeclineBooking(owner.getId(), bookingResponseDto.getId(), true))
                .thenReturn(bookingResponseDto);


    }

    @Test
    void getOneBooking() {
    }

    @Test
    void getAllUserBookings() {
    }

    @Test
    void getAllOwnerBookings() {
    }
}