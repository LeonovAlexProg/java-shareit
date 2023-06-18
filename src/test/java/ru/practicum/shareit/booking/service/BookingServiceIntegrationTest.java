package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingServiceIntegrationTest {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingService bookingService;

    Item item;
    User owner;
    User booker;
    LocalDateTime localDateTime;
    BookingRequestDto bookingDto;


    @BeforeEach
    void setUp() {
        owner = User.builder()
                .name("zima")
                .email("zimablue@gmail.com")
                .build();

        booker = User.builder()
                .name("miscuzi")
                .email("miscuzimiscuzi@gmail.com")
                .build();

        item = Item.builder()
                .name("drill")
                .description("drilling drill")
                .user(owner)
                .isAvailable(true)
                .build();

        localDateTime = LocalDateTime.now();

        bookingDto = BookingRequestDto.builder()
                .itemId(1L)
                .bookerId(2L)
                .start(localDateTime.plusDays(1))
                .end(localDateTime.plusDays(2))
                .build();
    }

    @Test
    void bookItem() {
        userRepository.save(owner);
        UserDto bookerDto = UserMapper.userDtoOf(userRepository.save(booker));
        ItemDto itemDto = ItemMapper.itemDtoOf(itemRepository.save(item));

        BookingResponseDto bookingResponseDto = bookingService.bookItem(bookingDto);

        Assertions.assertEquals(1L, bookingResponseDto.getId());
        Assertions.assertEquals(localDateTime.plusDays(1), bookingResponseDto.getStart());
        Assertions.assertEquals(localDateTime.plusDays(2), bookingResponseDto.getEnd());
        Assertions.assertEquals(bookerDto, bookingResponseDto.getBooker());
        Assertions.assertEquals(itemDto, bookingResponseDto.getItem());
    }

    @Test
    void acceptOrDeclineBooking() {
        UserDto ownerDto = UserMapper.userDtoOf(userRepository.save(owner));
        UserDto bookerDto = UserMapper.userDtoOf(userRepository.save(booker));
        ItemDto itemDto = ItemMapper.itemDtoOf(itemRepository.save(item));
        BookingResponseDto bookingResponseDto = bookingService.bookItem(bookingDto);

        BookingResponseDto acceptedBookingResponseDto = bookingService.acceptOrDeclineBooking(
                ownerDto.getId(),
                bookingResponseDto.getId(),
                true);

        Assertions.assertEquals("APPROVED", acceptedBookingResponseDto.getStatus());
    }

    @Test
    void getBooking() {
        BookingResponseDto actualBookingResponseDto;

        UserDto ownerDto = UserMapper.userDtoOf(userRepository.save(owner));
        UserDto bookerDto = UserMapper.userDtoOf(userRepository.save(booker));
        ItemDto itemDto = ItemMapper.itemDtoOf(itemRepository.save(item));
        BookingResponseDto bookingResponseDto = bookingService.bookItem(bookingDto);

        actualBookingResponseDto = bookingService.getBooking(ownerDto.getId(), bookingResponseDto.getId());

        Assertions.assertEquals(bookerDto, actualBookingResponseDto.getBooker());
        Assertions.assertEquals(itemDto, actualBookingResponseDto.getItem());

        actualBookingResponseDto = bookingService.getBooking(bookerDto.getId(), bookingResponseDto.getId());

        Assertions.assertEquals(bookerDto, actualBookingResponseDto.getBooker());
        Assertions.assertEquals(itemDto, actualBookingResponseDto.getItem());
    }

    @Test
    void getUserBookings() {
        List<BookingResponseDto> actualBookingList;

        UserDto ownerDto = UserMapper.userDtoOf(userRepository.save(owner));
        UserDto bookerDto = UserMapper.userDtoOf(userRepository.save(booker));
        ItemDto itemDto = ItemMapper.itemDtoOf(itemRepository.save(item));
        BookingResponseDto bookingResponseDto = bookingService.bookItem(bookingDto);

        actualBookingList = bookingService.getUserBookings(
                ownerDto.getId(),
                "ALL",
                true,
                null,
                null
        );

        Assertions.assertEquals(actualBookingList.size(), 1);
        Assertions.assertEquals(bookingResponseDto.getId(), actualBookingList.get(0).getId());
        Assertions.assertEquals(bookingResponseDto.getStart(), actualBookingList.get(0).getStart());
        Assertions.assertEquals(bookingResponseDto.getEnd(), actualBookingList.get(0).getEnd());
        Assertions.assertEquals(bookerDto, actualBookingList.get(0).getBooker());
        Assertions.assertEquals(itemDto, actualBookingList.get(0).getItem());
    }
}