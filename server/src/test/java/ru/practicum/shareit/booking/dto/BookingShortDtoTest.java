package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

class BookingShortDtoTest {
    User user = User.builder()
            .id(1L)
            .name("zima")
            .email("zimablue@mail.ru")
            .build();
    Item item = Item.builder()
            .id(1L)
            .user(user)
            .name("drill")
            .description("drilling drill")
            .isAvailable(true)
            .build();

    LocalDateTime localDateTime = LocalDateTime.now();

    @Test
    void of() {
        BookingShortDto expectedDto;
        BookingShortDto actualDto;

        expectedDto = BookingShortDto.builder()
                .id(1L)
                .bookerId(user.getId())
                .build();

        Booking booking = Booking.builder()
                .id(1L)
                .user(user)
                .item(item)
                .start(localDateTime.minusDays(1))
                .end(localDateTime.plusDays(1))
                .status(Booking.Status.WAITING)
                .build();

        actualDto = BookingMapper.shortResponseDtoOf(booking);

        Assertions.assertEquals(expectedDto, actualDto);
    }
}