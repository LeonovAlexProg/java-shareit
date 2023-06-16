package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingRepositoryTest {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;

    User booker;

    User owner;
    User ownerTwo;

    Item item;
    Item itemTwo;

    Booking booking;
    Booking bookingTwo;

    @BeforeEach
    void setUp() {
        LocalDateTime localDateTime = LocalDateTime.now();

        booker = User.builder()
                .name("zima")
                .email("zimablue@gmail.com")
                .build();
        userRepository.save(booker);

        owner = User.builder()
                .name("adam")
                .email("adamsandler@gmail.com")
                .build();
        ownerTwo = User.builder()
                .name("ivan")
                .email("skvorzov@gmail.com")
                .build();
        userRepository.save(owner);
        userRepository.save(ownerTwo);

        item = Item.builder()
                .name("drill")
                .description("drill for drilling")
                .isAvailable(true)
                .user(owner)
                .build();
        itemTwo = Item.builder()
                .name("shovel")
                .description("shovel for digging")
                .isAvailable(true)
                .user(ownerTwo)
                .build();
        itemRepository.save(item);
        itemRepository.save(itemTwo);


        booking = Booking.builder()
                .user(booker)
                .item(item)
                .status(Booking.Status.WAITING)
                .start(localDateTime.plusDays(5))
                .end(localDateTime.plusDays(10))
                .build();
        bookingTwo = Booking.builder()
                .user(booker)
                .item(itemTwo)
                .status(Booking.Status.WAITING)
                .start(localDateTime.plusDays(1))
                .end(localDateTime.plusDays(2))
                .build();
    }

    @Test
    void isBooker() {
        boolean expectedValue = true;
        boolean actualValue;

        bookingRepository.save(booking);

        actualValue = bookingRepository.isBooker(booker.getId(), booking.getId());

        Assertions.assertEquals(expectedValue, actualValue);
    }

    @Test
    void isOwner() {
        boolean expectedValue = true;
        boolean actualValue;

        bookingRepository.save(booking);

        actualValue = bookingRepository.isOwner(owner.getId(), booking.getId());

        Assertions.assertEquals(expectedValue, actualValue);
    }

    @Test
    void findAllByUser() {
        List<Booking> expectedList;
        List<Booking> actualList;

        bookingRepository.save(booking);

        expectedList = List.of(booking);
        actualList = bookingRepository.findAllByUser(booker.getId(), Pageable.unpaged());

        Assertions.assertEquals(expectedList, actualList);
    }

    @Test
    void findAllByOwner() {
        List<Booking> expectedList;
        List<Booking> actualList;

        bookingRepository.save(booking);

        expectedList = List.of(booking);
        actualList = bookingRepository.findAllByOwner(owner.getId(), Pageable.unpaged());

        Assertions.assertEquals(expectedList, actualList);
    }

    @Test
    void findAllCurrentBookingsByUser() {
        List<Booking> expectedList;
        List<Booking> actualList;

        booking.setStart(LocalDateTime.now().minusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(1));
        bookingTwo.setStart(LocalDateTime.now().minusDays(1));
        bookingTwo.setEnd(LocalDateTime.now().plusDays(1));

        bookingRepository.save(booking);
        bookingRepository.save(bookingTwo);

        expectedList = List.of(booking, bookingTwo);
        actualList = bookingRepository.findAllCurrentBookingsByUser(booker.getId(), LocalDateTime.now(), Pageable.unpaged());

        Assertions.assertEquals(expectedList, actualList);
    }

    @Test
    void findAllCurrentBookingsByOwner() {
        List<Booking> expectedList;
        List<Booking> actualList;

        booking.setStart(LocalDateTime.now().minusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(1));
        bookingTwo.setStart(LocalDateTime.now().minusDays(1));
        bookingTwo.setEnd(LocalDateTime.now().plusDays(1));

        bookingRepository.save(booking);
        bookingRepository.save(bookingTwo);

        expectedList = List.of(booking);
        actualList = bookingRepository.findAllCurrentBookingsByOwner(owner.getId(), LocalDateTime.now(), Pageable.unpaged());

        Assertions.assertEquals(expectedList, actualList);
    }

    @Test
    void findAllFutureBookingsByUser() {
        List<Booking> expectedList;
        List<Booking> actualList;

        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        bookingTwo.setStart(LocalDateTime.now().plusDays(1));
        bookingTwo.setEnd(LocalDateTime.now().plusDays(2));

        bookingRepository.save(booking);
        bookingRepository.save(bookingTwo);

        expectedList = new ArrayList<>(List.of(booking, bookingTwo));
        expectedList.sort(Comparator.comparing(Booking::getStart).reversed());
        actualList = bookingRepository.findAllFutureBookingsByUser(booker.getId(), LocalDateTime.now(), Pageable.unpaged());

        Assertions.assertEquals(expectedList, actualList);
    }

    @Test
    void findAllFutureBookingsByOwner() {
        List<Booking> expectedList;
        List<Booking> actualList;

        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        bookingTwo.setStart(LocalDateTime.now().plusDays(1));
        bookingTwo.setEnd(LocalDateTime.now().plusDays(2));

        bookingRepository.save(booking);
        bookingRepository.save(bookingTwo);

        expectedList = List.of(booking);
        actualList = bookingRepository.findAllFutureBookingsByOwner(owner.getId(), LocalDateTime.now(), Pageable.unpaged());

        Assertions.assertEquals(expectedList, actualList);
    }

    @Test
    void findAllPastBookingsByUser() {
        List<Booking> expectedList;
        List<Booking> actualList;

        booking.setStart(LocalDateTime.now().minusDays(2));
        booking.setEnd(LocalDateTime.now().minusDays(1));
        bookingTwo.setStart(LocalDateTime.now().minusDays(2));
        bookingTwo.setEnd(LocalDateTime.now().minusDays(1));

        bookingRepository.save(booking);
        bookingRepository.save(bookingTwo);

        expectedList = List.of(booking, bookingTwo);
        actualList = bookingRepository.findAllPastBookingsByUser(booker.getId(), LocalDateTime.now(), Pageable.unpaged());

        Assertions.assertEquals(expectedList, actualList);
    }

    @Test
    void findAllPastBookingsByOwner() {
        List<Booking> expectedList;
        List<Booking> actualList;

        booking.setStart(LocalDateTime.now().minusDays(2));
        booking.setEnd(LocalDateTime.now().minusDays(1));
        bookingTwo.setStart(LocalDateTime.now().minusDays(2));
        bookingTwo.setEnd(LocalDateTime.now().minusDays(1));

        bookingRepository.save(booking);
        bookingRepository.save(bookingTwo);

        expectedList = List.of(booking);
        actualList = bookingRepository.findAllPastBookingsByOwner(owner.getId(), LocalDateTime.now(), Pageable.unpaged());

        Assertions.assertEquals(expectedList, actualList);
    }

    @Test
    void findLastBooking() {
        Booking expectedBooking;
        Booking actualBooking;

        bookingTwo.setItem(item);

        booking.setStart(LocalDateTime.now().minusDays(2));
        booking.setEnd(LocalDateTime.now().minusDays(1));
        bookingTwo.setStart(LocalDateTime.now().minusDays(4));
        bookingTwo.setEnd(LocalDateTime.now().minusDays(3));

        booking.setStatus(Booking.Status.APPROVED);
        bookingTwo.setStatus(Booking.Status.APPROVED);

        bookingRepository.save(booking);
        bookingRepository.save(bookingTwo);

        expectedBooking = booking;
        actualBooking = bookingRepository.findLastBooking(item.getId());

        Assertions.assertEquals(expectedBooking, actualBooking);
    }

    @Test
    void findNextBooking() {
        Booking expectedBooking;
        Booking actualBooking;

        booking.setItem(itemTwo);

        booking.setStart(LocalDateTime.now().plusDays(3));
        booking.setEnd(LocalDateTime.now().plusDays(4));
        bookingTwo.setStart(LocalDateTime.now().plusDays(1));
        bookingTwo.setEnd(LocalDateTime.now().plusDays(2));

        booking.setStatus(Booking.Status.APPROVED);
        bookingTwo.setStatus(Booking.Status.APPROVED);

        bookingRepository.save(booking);
        bookingRepository.save(bookingTwo);

        expectedBooking = bookingTwo;
        actualBooking = bookingRepository.findNextBooking(itemTwo.getId());

        Assertions.assertEquals(expectedBooking, actualBooking);
    }
}