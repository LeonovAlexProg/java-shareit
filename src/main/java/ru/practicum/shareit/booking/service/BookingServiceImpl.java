package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.exceptions.AcceptBookingException;
import ru.practicum.shareit.booking.exceptions.BookingValidationException;
import ru.practicum.shareit.booking.exceptions.BookingNotFoundException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.exceptions.ItemBookingException;
import ru.practicum.shareit.item.exceptions.ItemNotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.item.model.Item;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public BookingResponseDto bookItem(BookingRequestDto bookingDto) {
        if (bookingDto.getStart().isAfter(bookingDto.getEnd()) ||
                bookingDto.getStart().isEqual(bookingDto.getEnd()))
            throw new BookingValidationException("start/end data is incorrect");

        User user = userRepository.findById(bookingDto.getBookerId())
                .orElseThrow(() -> new UserNotFoundException(String.format("User id %d not found", bookingDto.getBookerId())));
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new ItemNotFoundException(String.format("Item id %d not found", bookingDto.getItemId())));

        if (item.getIsAvailable()) {
            Booking booking = Booking.builder()
                    .start(bookingDto.getStart())
                    .end(bookingDto.getEnd())
                    .user(user)
                    .item(item)
                    .status(Booking.Status.WAITING)
                    .build();

            return BookingResponseDto.of(bookingRepository.save(booking));
        } else {
            throw new ItemBookingException(String.format("Item id %d is unavailable", item.getId()));
        }
    }

    @Override
    @Transactional
    public BookingResponseDto acceptOrDeclineBooking(long userId, long bookingId, boolean approved) {
        if (!userRepository.existsById(userId))
            throw new UserNotFoundException(String.format("User id %d not found", userId));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(String.format("Booking id %d not found", bookingId)));

        if (userId != booking.getItem().getUser().getId())
            throw new BookingValidationException(String.format("User id %d has no rights to approve booking id %d", userId, bookingId));

        if (booking.getStatus().equals(Booking.Status.WAITING) && booking.getItem().getIsAvailable()) {
            booking.setStatus(Booking.Status.APPROVED);
            booking.getItem().setIsAvailable(Boolean.TRUE);

            bookingRepository.save(booking);
        } else {
            throw new AcceptBookingException(String.format("Unable to approve booking"));
        }

        return BookingResponseDto.of(booking);
    }
}
