package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import java.util.List;

public interface BookingService {
    BookingResponseDto bookItem(BookingRequestDto bookingDto);

    BookingResponseDto acceptOrDeclineBooking(long userId, long bookingId, boolean approved);

    BookingResponseDto getBooking(long userId, long bookingId);

    List<BookingResponseDto> getUserBookings(long userId, String state, boolean isOwner);
}
