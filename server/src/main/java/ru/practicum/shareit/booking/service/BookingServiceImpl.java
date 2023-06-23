package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.exceptions.AcceptBookingException;
import ru.practicum.shareit.booking.exceptions.BookingNotFoundException;
import ru.practicum.shareit.booking.exceptions.BookingValidationException;
import ru.practicum.shareit.booking.exceptions.ItemBookingException;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.exceptions.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.exception.PaginationDataException;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public BookingResponseDto bookItem(BookingRequestDto bookingDto) {
        if (bookingDto.getStart().isAfter(bookingDto.getEnd()) ||
                bookingDto.getStart().isEqual(bookingDto.getEnd()))
            throw new BookingValidationException("start/end data is incorrect");

        User user = userRepository.findById(bookingDto.getBookerId())
                .orElseThrow(() -> new UserNotFoundException(String.format("User id %d not found", bookingDto.getBookerId())));
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new ItemNotFoundException(String.format("Item id %d not found", bookingDto.getItemId())));

        if (item.getUser().equals(user))
            throw new BookingNotFoundException(String.format("Item id %d already booked", item.getId()));

        if (item.getIsAvailable()) {
            Booking booking = Booking.builder()
                    .start(bookingDto.getStart())
                    .end(bookingDto.getEnd())
                    .user(user)
                    .item(item)
                    .status(Booking.Status.WAITING)
                    .build();

            return BookingMapper.responseDtoOf(bookingRepository.save(booking));
        } else {
            throw new ItemBookingException(String.format("Item id %d is unavailable", item.getId()));
        }
    }

    @Override
    @Transactional
    public BookingResponseDto acceptOrDeclineBooking(long userId, long bookingId, boolean isApproved) {
        if (!userRepository.existsById(userId))
            throw new UserNotFoundException(String.format("User id %d not found", userId));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(String.format("Booking id %d not found", bookingId)));
        Item item = booking.getItem();

        if (userId != booking.getItem().getUser().getId())
            throw new BookingNotFoundException(String.format("User id %d has no rights to approve booking id %d", userId, bookingId));

        if (booking.getStatus().equals(Booking.Status.WAITING) && booking.getItem().getIsAvailable()) {
            if (isApproved) {
                booking.setStatus(Booking.Status.APPROVED);
//              booking.getItem().setIsAvailable(Boolean.TRUE);
            } else {
                booking.setStatus(Booking.Status.REJECTED);
            }
            bookingRepository.save(booking);
        } else {
            throw new AcceptBookingException("Unable to approve booking");
        }

        return BookingMapper.responseDtoOf(booking);
    }

    @Override
    public BookingResponseDto getBooking(long userId, long bookingId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("User id %d not found", userId)));
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(String.format("Booking id %d not found", bookingId)));

        if (!bookingRepository.isBooker(userId, bookingId) && !bookingRepository.isOwner(userId, bookingId))
            throw new BookingNotFoundException(String.format("No booking id %d found for user %d",bookingId, userId));


        return BookingMapper.responseDtoOf(booking);
    }

    @Override
    public List<BookingResponseDto> getUserBookings(long userId, String state, boolean isOwner, Integer from, Integer size) {
        if (!userRepository.existsById(userId))
            throw new UserNotFoundException(String.format("User id %d not found", userId));

        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings;
        Pageable pageable;

        if (from != null && size != null) {
            if (from < 0 || size < 0) {
                throw new PaginationDataException("Invalid pagination data");
            }

            int page = from / size;
            pageable = PageRequest.of(page, size);
        } else {
            pageable = Pageable.unpaged();
        }


        switch (state) {
            case "ALL":
                if (isOwner)
                    bookings = bookingRepository.findAllByOwner(userId, pageable);
                else
                    bookings = bookingRepository.findAllByUser(userId, pageable);
                break;
            case "CURRENT":
                if (isOwner)
                    bookings = bookingRepository.findAllCurrentBookingsByOwner(userId, now, pageable);
                else
                    bookings = bookingRepository.findAllCurrentBookingsByUser(userId, now, pageable);
                break;
            case "FUTURE":
                if (isOwner)
                    bookings = bookingRepository.findAllFutureBookingsByOwner(userId, now, pageable);
                else
                    bookings = bookingRepository.findAllFutureBookingsByUser(userId, now, pageable);
                break;
            case "PAST":
                if (isOwner)
                    bookings = bookingRepository.findAllPastBookingsByOwner(userId, now, pageable);
                else
                    bookings = bookingRepository.findAllPastBookingsByUser(userId, now, pageable);
                break;
            case "WAITING":
                if (isOwner)
                    bookings = bookingRepository.findAllByItemUserIdAndStatusIsOrderByStartDesc(userId, Booking.Status.WAITING, pageable);
                else
                    bookings = bookingRepository.findAllByUserIdIsAndStatusIsOrderByStartDesc(userId, Booking.Status.WAITING, pageable);
                break;
            case "REJECTED":
                if (isOwner)
                    bookings = bookingRepository.findAllByItemUserIdAndStatusIsOrderByStartDesc(userId, Booking.Status.REJECTED, pageable);
                else
                    bookings = bookingRepository.findAllByUserIdIsAndStatusIsOrderByStartDesc(userId, Booking.Status.REJECTED, pageable);
                break;
            default:
                throw new BookingValidationException("Unknown state: UNSUPPORTED_STATUS");
        }

        return BookingMapper.responseDtoListOf(bookings);
    }
}
