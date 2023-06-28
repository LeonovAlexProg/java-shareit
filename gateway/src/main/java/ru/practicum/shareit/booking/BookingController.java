package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
	private final BookingClient bookingClient;

	@PostMapping
	public ResponseEntity<Object> bookItem(@RequestHeader("X-Sharer-User-Id") long userId,
			@RequestBody @Valid BookingRequestDto requestDto) {
		log.info("Create booking by user id {}", userId);
		return bookingClient.bookItem(userId, requestDto);
	}

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> acceptOrDeclineBooking(@RequestHeader(name = "X-Sharer-User-Id") long userId,
                                                     @PathVariable long bookingId,
                                                     @RequestParam boolean approved) {
        if (approved) {
            log.info("Accept booking id {} by user id {}", bookingId, userId);
        } else {
            log.info("Decline booking id {} by user id {}", bookingId, userId);
        }
        return bookingClient.acceptOrDeclineBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getOneBooking(@RequestHeader(name = "X-Sharer-User-id") long userId,
                                                @PathVariable long bookingId) {
        log.info("Get booking id {} by user id {}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping()
    public ResponseEntity<Object> getAllUserBookings(@RequestHeader(name = "X-Sharer-User-id") long userId,
                                                       @RequestParam(defaultValue = "ALL") String state,
                                                       @Positive @RequestParam(required = false) Integer from,
                                                       @Positive @RequestParam(required = false) Integer size) {
        log.info("Get all user bookings state {} user id {} from {} size {}", state, userId, from, size);
        return bookingClient.getUserBookings(userId, state, from, size, false);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllOwnerBookings(@RequestHeader(name = "X-Sharer-User-id") long userId,
                                                        @RequestParam(defaultValue = "ALL") String state,
                                                        @Positive @RequestParam(required = false) Integer from,
                                                        @Positive @RequestParam(required = false) Integer size) {
        log.info("Get all owner bookings state {} user id {} from {} size {}", state, userId, from, size);
        return bookingClient.getUserBookings(userId, state, from, size, true);
    }
}
