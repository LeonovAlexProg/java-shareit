package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private final BookingService bookingService;

    @PostMapping()
    public BookingResponseDto bookNewItem(@RequestHeader(name = "X-Sharer-User-Id") long userId,
                                          @RequestBody BookingRequestDto bookingDto) {
        log.info("Creating booking by user id {}", userId);
        bookingDto.setBookerId(userId);
        return bookingService.bookItem(bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto acceptOrDeclineBooking(@RequestHeader(name = "X-Sharer-User-Id") long userId,
                                                     @PathVariable long bookingId,
                                                     @RequestParam boolean approved) {
        if (approved) {
            log.info("Accepting booking id {} by user id {}", bookingId, userId);
        } else {
            log.info("Declining booking id {} by user id {}", bookingId, userId);
        }
        return bookingService.acceptOrDeclineBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getOneBooking(@RequestHeader(name = "X-Sharer-User-id") long userId,
                                            @PathVariable long bookingId) {
        log.info("Geting booking id {} by user id {}", bookingId, userId);
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping()
    public List<BookingResponseDto> getAllUserBookings(@RequestHeader(name = "X-Sharer-User-id") long userId,
                                                       @RequestParam(defaultValue = "ALL") String state,
                                                       @RequestParam(required = false) Integer from,
                                                       @RequestParam(required = false) Integer size) {
        log.info("Getting all user bookings state {} user id {} from {} size {}", state, userId, from, size);
        return bookingService.getUserBookings(userId, state, false, from, size);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getAllOwnerBookings(@RequestHeader(name = "X-Sharer-User-id") long userId,
                                                        @RequestParam(defaultValue = "ALL") String state,
                                                        @RequestParam(required = false) Integer from,
                                                        @RequestParam(required = false) Integer size) {
        log.info("Getting all owner bookings state {} user id {} from {} size {}", state, userId, from, size);
        return bookingService.getUserBookings(userId, state, true, from, size);
    }
}
