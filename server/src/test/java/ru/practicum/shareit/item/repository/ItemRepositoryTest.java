package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemRepositoryTest {
    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    private BookingRepository bookingRepository;

    User owner;
    Item item;
    Item itemTwo;

    @BeforeEach
    void setUp() {
        owner = User.builder()
                .name("zima")
                .email("zimablue@gmail.com")
                .build();
        userRepository.save(owner);

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
                .user(owner)
                .build();
    }

    @Test
    void findItemsLike() {
        Item expectedItem = item;
        Item actualItem;

        itemRepository.save(item);
        itemRepository.save(itemTwo);

        actualItem = itemRepository.findItemsLike("drill").get().get(0);

        Assertions.assertEquals(expectedItem, actualItem);
    }

    @Test
    void itemWasRentedByUser() {
        itemRepository.save(item);

        User booker = User.builder()
                .name("ivan")
                .email("skvorzov@gmail.com")
                .build();
        userRepository.save(booker);

        Booking booking = Booking.builder()
                .user(booker)
                .item(item)
                .status(Booking.Status.WAITING)
                .start(LocalDateTime.now().minusDays(5))
                .end(LocalDateTime.now().minusDays(1))
                .build();
        bookingRepository.save(booking);

        Assertions.assertTrue(itemRepository.itemWasRentedByUser(item.getId(), booker.getId(), LocalDateTime.now()));
    }
}