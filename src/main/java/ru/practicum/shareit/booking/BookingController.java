package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping()
    public BookingResponseDto bookNewItem(@RequestHeader(name = "X-Sharer-User-Id") long userId,
                                          @Valid @RequestBody BookingRequestDto bookingDto) {
        bookingDto.setBookerId(userId);
        return bookingService.bookItem(bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto acceptOrDeclineBooking(@RequestHeader(name = "X-Sharer-User-Id") long userId,
                                                     @PathVariable long bookingId,
                                                     @RequestParam boolean approved) {
        return bookingService.acceptOrDeclineBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getOneBooking(@RequestHeader(name = "X-Sharer-User-id") long userId,
                                            @PathVariable long bookingId) {
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping()
    public List<BookingResponseDto> getAllUserBookings(@RequestHeader(name = "X-Sharer-User-id") long userId,
                                                       @RequestParam(required = false, defaultValue = "ALL") String state,
                                                       @RequestParam(required = false) Integer from,
                                                       @RequestParam(required = false) Integer size) {
        return bookingService.getUserBookings(userId, state, false, from, size);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getAllOwnerBookings(@RequestHeader(name = "X-Sharer-User-id") long userId,
                                                       @RequestParam(required = false, defaultValue = "ALL") String state,
                                                        @RequestParam(required = false) Integer from,
                                                        @RequestParam(required = false) Integer size) {
        return bookingService.getUserBookings(userId, state, true, from, size);
    }
}
