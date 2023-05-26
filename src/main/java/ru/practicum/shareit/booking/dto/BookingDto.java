package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.constraints.BookingCreateConstraint;
import ru.practicum.shareit.booking.constraints.BookingIdConstraint;
import ru.practicum.shareit.booking.model.Booking;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.time.LocalDateTime;

@Builder
@Data
public class BookingDto {
    @Null(groups = BookingIdConstraint.class)
    Long id;

    @NotNull(groups = BookingCreateConstraint.class)
    @FutureOrPresent(groups = BookingCreateConstraint.class)
    LocalDateTime start;

    @NotNull(groups = BookingCreateConstraint.class)
    @Future(groups = BookingCreateConstraint.class)
    LocalDateTime end;

    @Null(groups = BookingCreateConstraint.class)
    String status;

    @Null(groups = BookingCreateConstraint.class)
    Long bookerId;

    @NotNull(groups = BookingCreateConstraint.class)
    Long itemId;

    @Null(groups = BookingCreateConstraint.class)
    String itemName;

    public static BookingDto of(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus().toString())
                .bookerId(builder().bookerId)
                .itemId(booking.getItem().getId())
                .itemName(booking.getItem().getName())
                .build();
    }
}
