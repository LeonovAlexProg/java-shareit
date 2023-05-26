package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.constraints.BookingCreateConstraint;
import ru.practicum.shareit.booking.constraints.BookingIdConstraint;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.time.LocalDateTime;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping()
    public BookingDto bookNewItem(@RequestHeader(name = "X-Sharer-User-Id") long userId,
            @Validated(value = {BookingCreateConstraint.class, BookingIdConstraint.class}) @RequestBody BookingDto bookingDto) {
        bookingDto.setBookerId(userId);
        return bookingService.bookItem(bookingDto);
    }
}
