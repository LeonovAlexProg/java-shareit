package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.Assert;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.exceptions.AcceptBookingException;
import ru.practicum.shareit.booking.exceptions.BookingNotFoundException;
import ru.practicum.shareit.booking.exceptions.ItemBookingException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemResponseDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplUnitTest {
    @InjectMocks
    BookingServiceImpl bookingService;
    @Mock
    BookingRepository bookingRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    ItemRepository itemRepository;

    BookingRequestDto bookingRequestDto;

    User booker;
    User owner;
    Item item;
    Booking booking;
    Booking bookingSaved;


    @BeforeEach
    public void init() {
        bookingRequestDto = BookingRequestDto.builder()
                .itemId(1L)
                .bookerId(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(3))
                .build();

        booker = User.builder()
                .id(1L)
                .name("Zima")
                .email("zimablue@gmail.com")
                .build();

        owner = User.builder()
                .id(2L)
                .name("Alex")
                .email("alex@gmail.com")
                .build();

        item = Item.builder()
                .id(1L)
                .name("Drill")
                .user(owner)
                .description("Drill to drill drillable things")
                .isAvailable(true)
                .build();

        booking = Booking.builder()
                .start(bookingRequestDto.getStart())
                .end(bookingRequestDto.getEnd())
                .user(booker)
                .item(item)
                .status(Booking.Status.WAITING)
                .build();

        bookingSaved = Booking.builder()
                .id(1L)
                .start(bookingRequestDto.getStart())
                .end(bookingRequestDto.getEnd())
                .user(booker)
                .item(item)
                .status(Booking.Status.WAITING)
                .build();
    }

    @Test
    void bookItem() {
        BookingResponseDto expectedResponseDto;
        BookingResponseDto actualResponseDto;

        expectedResponseDto = BookingResponseDto.of(bookingSaved);

        Mockito
                .when(userRepository.findById(booker.getId()))
                .thenReturn(Optional.ofNullable(booker));
        Mockito
                .when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.ofNullable(item));
        Mockito
                .when(bookingRepository.save(booking))
                .thenReturn(bookingSaved);

        actualResponseDto = bookingService.bookItem(bookingRequestDto);

        Assertions.assertEquals(expectedResponseDto, actualResponseDto);
    }

    @Test
    void bookItemAlreadyBooked() {
        String expectedMessage = "Item id 1 already booked";
        String actualMessage;

        Mockito
                .when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.ofNullable(owner));
        Mockito
                .when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.ofNullable(item));

        bookingRequestDto.setBookerId(2L);

        Exception exception = Assertions.assertThrows(BookingNotFoundException.class,
                () -> bookingService.bookItem(bookingRequestDto));
        actualMessage = exception.getMessage();

        Assertions.assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void bookItemUnavailable() {
        String expectedMessage = "Item id 1 is unavailable";
        String actualMessage;

        Mockito
                .when(userRepository.findById(booker.getId()))
                .thenReturn(Optional.ofNullable(booker));
        Mockito
                .when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.ofNullable(item));

        item.setIsAvailable(false);

        Exception exception = Assertions.assertThrows(ItemBookingException.class,
                () -> bookingService.bookItem(bookingRequestDto));
        actualMessage = exception.getMessage();

        Assertions.assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void acceptOrDeclineBooking() {
        BookingResponseDto expectedResponse;
        BookingResponseDto actualResponse;

        Mockito
                .when(userRepository.existsById(owner.getId()))
                .thenReturn(true);
        Mockito
                .when(bookingRepository.findById(bookingSaved.getId()))
                .thenReturn(Optional.ofNullable(bookingSaved));

        actualResponse = bookingService.acceptOrDeclineBooking(owner.getId(), bookingSaved.getId(), true);
        bookingSaved.setStatus(Booking.Status.APPROVED);
        expectedResponse = BookingResponseDto.of(bookingSaved);

        Assertions.assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void acceptOrDeclineBookingNoRightToApprove() {
        String expectedMessage = "User id 1 has no rights to approve booking id 1";
        String actualMessage;

        Mockito
                .when(userRepository.existsById(booker.getId()))
                .thenReturn(true);
        Mockito
                .when(bookingRepository.findById(bookingSaved.getId()))
                .thenReturn(Optional.ofNullable(bookingSaved));

        Exception exception = Assertions.assertThrows(BookingNotFoundException.class,
                () -> bookingService.acceptOrDeclineBooking(booker.getId(), bookingSaved.getId(), true));
        actualMessage = exception.getMessage();

        Assertions.assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void acceptOrDeclineUnableToApprove() {
        String expectedMessage = "Unable to approve booking";
        String actualMessage;


        Mockito
                .when(userRepository.existsById(owner.getId()))
                .thenReturn(true);
        Mockito
                .when(bookingRepository.findById(bookingSaved.getId()))
                .thenReturn(Optional.ofNullable(bookingSaved));
        item.setIsAvailable(false);

        Exception exception = Assertions.assertThrows(AcceptBookingException.class,
                () -> bookingService.acceptOrDeclineBooking(owner.getId(), bookingSaved.getId(), true));
        actualMessage = exception.getMessage();

        Assertions.assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void getBooking() {
        BookingResponseDto expectedResponse;
        BookingResponseDto actualResponse;

        Mockito
                .when(userRepository.findById(booker.getId()))
                .thenReturn(Optional.ofNullable(booker));
        Mockito
                .when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.ofNullable(owner));
        Mockito
                .when(bookingRepository.save(booking))
                .thenReturn(bookingSaved);
        Mockito
                .when(bookingRepository.isBooker(booker.getId(), bookingSaved.getId()))
                .thenReturn(true);
        Mockito
                .when(bookingRepository.isOwner(owner.getId(), bookingSaved.getId()))
                .thenReturn(true);

        expectedResponse = BookingResponseDto.of(bookingSaved);
        actualResponse = bookingService.getBooking(owner.getId(), bookingSaved.getId());

        Assertions.assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void getUserBookings() {
    }
}