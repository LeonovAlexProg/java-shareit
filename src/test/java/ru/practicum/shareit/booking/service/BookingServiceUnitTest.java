package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.exceptions.AcceptBookingException;
import ru.practicum.shareit.booking.exceptions.BookingNotFoundException;
import ru.practicum.shareit.booking.exceptions.BookingValidationException;
import ru.practicum.shareit.booking.exceptions.ItemBookingException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.exception.PaginationDataException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class BookingServiceUnitTest {
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


        Mockito
                .when(userRepository.findById(booker.getId()))
                .thenReturn(Optional.ofNullable(booker));
        Mockito
                .when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.ofNullable(item));
        Mockito
                .when(bookingRepository.save(booking))
                .thenReturn(bookingSaved);

        expectedResponseDto = BookingResponseDto.of(bookingSaved);
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
                .when(bookingRepository.findById(bookingSaved.getId()))
                .thenReturn(Optional.ofNullable(bookingSaved));
        Mockito
                .when(bookingRepository.isBooker(booker.getId(), bookingSaved.getId()))
                .thenReturn(false);
        Mockito
                .when(bookingRepository.isBooker(owner.getId(), bookingSaved.getId()))
                .thenReturn(true);

        expectedResponse = BookingResponseDto.of(bookingSaved);
        actualResponse = bookingService.getBooking(owner.getId(), bookingSaved.getId());

        Assertions.assertEquals(expectedResponse, actualResponse);

        Mockito
                .when(bookingRepository.isOwner(booker.getId(), bookingSaved.getId()))
                .thenReturn(true);

        actualResponse = bookingService.getBooking(booker.getId(), bookingSaved.getId());

        Assertions.assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void getBookingUnavailable() {
        String expectedMessage; //"No booking id %d found for user %d"
        String actualMessage;
        Exception exception;

        Mockito
                .when(userRepository.findById(booker.getId()))
                .thenReturn(Optional.ofNullable(booker));
        Mockito
                .when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.ofNullable(owner));
        Mockito
                .when(bookingRepository.findById(bookingSaved.getId()))
                .thenReturn(Optional.ofNullable(bookingSaved));

        Mockito
                .when(bookingRepository.isBooker(booker.getId(), bookingSaved.getId()))
                .thenReturn(false);
        Mockito
                .when(bookingRepository.isBooker(owner.getId(), bookingSaved.getId()))
                .thenReturn(false);

        expectedMessage = String.format("No booking id %d found for user %d", bookingSaved.getId(), owner.getId());
        exception = Assertions.assertThrows(BookingNotFoundException.class,
                () -> bookingService.getBooking(owner.getId(), bookingSaved.getId()));
        actualMessage = exception.getMessage();

        Assertions.assertEquals(expectedMessage, actualMessage);

        expectedMessage = String.format("No booking id %d found for user %d", bookingSaved.getId(), booker.getId());
        exception = Assertions.assertThrows(BookingNotFoundException.class,
                () -> bookingService.getBooking(booker.getId(), bookingSaved.getId()));
        actualMessage = exception.getMessage();

        Assertions.assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void getUserBookings() {
        List<BookingResponseDto> expectedList;
        List<BookingResponseDto> actualList;

        Mockito
                .when(userRepository.existsById(owner.getId()))
                .thenReturn(true);
        Mockito
                .when(bookingRepository.findAllByOwner(owner.getId(), Pageable.unpaged()))
                .thenReturn(List.of(bookingSaved));

        expectedList = BookingResponseDto.listOf(List.of(bookingSaved));
        actualList = bookingService.getUserBookings(owner.getId(), "ALL", true, null, null);

        Assertions.assertEquals(expectedList, actualList);

        Mockito
                .when(userRepository.existsById(booker.getId()))
                .thenReturn(true);
        Mockito
                .when(bookingRepository.findAllByUser(booker.getId(), Pageable.unpaged()))
                .thenReturn(List.of(bookingSaved));

        expectedList = BookingResponseDto.listOf(List.of(bookingSaved));
        actualList = bookingService.getUserBookings(booker.getId(), "ALL", false, null, null);

        Assertions.assertEquals(expectedList, actualList);
    }

    @Test
    void getUserBookingsInvalidPagination() {
        String expectedMessage = "Invalid pagination data";
        String actualMessage;

        Mockito
                .when(userRepository.existsById(owner.getId()))
                .thenReturn(true);

        Exception exception = Assertions.assertThrows(PaginationDataException.class,
                () -> bookingService.getUserBookings(owner.getId(), "ALL", true, -1, -1));
        actualMessage = exception.getMessage();

        Assertions.assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void getUserBookingsUnsupportedStatus() {
        String expectedMessage = "Unknown state: UNSUPPORTED_STATUS";
        String actualMessage;

        Mockito
                .when(userRepository.existsById(owner.getId()))
                .thenReturn(true);

        Exception exception = Assertions.assertThrows(BookingValidationException.class,
                () -> bookingService.getUserBookings(owner.getId(), "TEST_STATE", true, null, null));
        actualMessage = exception.getMessage();

        Assertions.assertEquals(expectedMessage, actualMessage);
    }
}