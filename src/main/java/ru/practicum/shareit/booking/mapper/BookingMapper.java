package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

public class BookingMapper {
    public static BookingResponseDto responseDtoOf(Booking booking) {
        return BookingResponseDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus().toString())
                .booker(UserMapper.userDtoOf(booking.getUser()))
                .item(ItemMapper.itemDtoOf(booking.getItem()))
                .build();
    }

    public static List<BookingResponseDto> responseDtoListOf(List<Booking> bookings) {
        return bookings.stream().map(BookingMapper::responseDtoOf).collect(Collectors.toList());
    }

    public static BookingShortDto shortResponseDtoOf(Booking booking) {
        return BookingShortDto.builder()
                .id(booking.getId())
                .bookerId(booking.getUser().getId())
                .build();
    }
}
