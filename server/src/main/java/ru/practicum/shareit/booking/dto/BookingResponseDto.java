package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.constraints.BookingCreateConstraint;
import ru.practicum.shareit.booking.constraints.BookingIdConstraint;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.time.LocalDateTime;

@Builder
@Data
public class BookingResponseDto {
    @Null(groups = BookingIdConstraint.class)
    private Long id;

    @NotNull(groups = BookingCreateConstraint.class)
    @FutureOrPresent(groups = BookingCreateConstraint.class)
    private LocalDateTime start;

    @NotNull(groups = BookingCreateConstraint.class)
    @Future(groups = BookingCreateConstraint.class)
    private LocalDateTime end;

    @Null(groups = BookingCreateConstraint.class)
    private String status;

    @Null(groups = BookingCreateConstraint.class)
    private UserDto booker;


    @NotNull(groups = BookingCreateConstraint.class)
    private ItemDto item;
}
