package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.Booking;

@Data
@Builder
public class BookingShortDto {
    Long id;
    Long bookerId;

    public static BookingShortDto of(Booking booking) {
        return BookingShortDto.builder()
                .id(booking.getId())
                .bookerId(booking.getUser().getId())
                .build();
    }
}
