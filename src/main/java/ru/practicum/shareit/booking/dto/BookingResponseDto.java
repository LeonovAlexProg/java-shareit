package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.constraints.BookingCreateConstraint;
import ru.practicum.shareit.booking.constraints.BookingIdConstraint;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
    UserDto booker;


    @NotNull(groups = BookingCreateConstraint.class)
    ItemDto item;

    public static BookingResponseDto of(Booking booking) {
        return BookingResponseDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus().toString())
                .booker(UserDto.of(booking.getUser()))
                .item(ItemDto.of(booking.getItem()))
                .build();
    }

    public static List<BookingResponseDto> listOf(List<Booking> bookings) {
        return bookings.stream().map(BookingResponseDto::of).collect(Collectors.toList());
    }
}
