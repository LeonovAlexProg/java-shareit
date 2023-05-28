package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

public interface BookingService {
    BookingResponseDto bookItem(BookingRequestDto bookingDto);

    BookingResponseDto acceptOrDeclineBooking(long userId, long bookingId, boolean approved);
}
