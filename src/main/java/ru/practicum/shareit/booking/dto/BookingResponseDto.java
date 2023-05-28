package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.constraints.BookingCreateConstraint;
import ru.practicum.shareit.booking.constraints.BookingIdConstraint;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.time.LocalDateTime;

@Builder
@Data
public class BookingResponseDto {
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
    User booker;


    @NotNull(groups = BookingCreateConstraint.class)
    Item item;

    public static BookingResponseDto of(Booking booking) {
        return BookingResponseDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus().toString())
                .booker(booking.getUser())
                .item(booking.getItem())
                .build();
    }
}