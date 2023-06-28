package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

class BookingResponseDtoTest {
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
        BookingResponseDto expectedDto;
        BookingResponseDto actualDto;

        expectedDto = BookingResponseDto.builder()
                .id(1L)
                .status("WAITING")
                .booker(UserMapper.userDtoOf(user))
                .item(ItemMapper.itemDtoOf(item))
                .start(localDateTime.minusDays(1))
                .end(localDateTime.plusDays(1))
                .build();

        Booking booking = Booking.builder()
                .id(1L)
                .user(user)
                .item(item)
                .start(localDateTime.minusDays(1))
                .end(localDateTime.plusDays(1))
                .status(Booking.Status.WAITING)
                .build();

        actualDto = BookingMapper.responseDtoOf(booking);

        Assertions.assertEquals(expectedDto, actualDto);
    }

    @Test
    void listOf() {
        List<BookingResponseDto> expectedList;
        List<BookingResponseDto> actualList;

        BookingResponseDto correctDto = BookingResponseDto.builder()
                .id(1L)
                .status("WAITING")
                .booker(UserMapper.userDtoOf(user))
                .item(ItemMapper.itemDtoOf(item))
                .start(localDateTime.minusDays(1))
                .end(localDateTime.plusDays(1))
                .build();

        Booking booking = Booking.builder()
                .id(1L)
                .user(user)
                .item(item)
                .start(localDateTime.minusDays(1))
                .end(localDateTime.plusDays(1))
                .status(Booking.Status.WAITING)
                .build();

        expectedList = List.of(correctDto, correctDto, correctDto);
        actualList = BookingMapper.responseDtoListOf(List.of(booking, booking, booking));

        Assertions.assertEquals(expectedList, actualList);
    }
}