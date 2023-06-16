package ru.practicum.shareit.request.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemRequestRepositoryTest {
    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRequestRepository itemRequestRepository;

    User requester;
    ItemRequest itemRequest;
    ItemRequest itemRequestTwo;

    @BeforeEach
    void setUp() {
        requester = User.builder()
                .name("zima")
                .email("zimablue@gmail.com")
                .build();
        userRepository.save(requester);

        itemRequest = ItemRequest.builder()
                .created(LocalDateTime.now())
                .description("i want POWER")
                .user(requester)
                .build();
        itemRequestTwo = ItemRequest.builder()
                .created(LocalDateTime.now())
                .description("i want MONEY")
                .user(requester)
                .build();
    }

    @Test
    void findAllByUserId() {
        List<ItemRequest> expectedList;
        List<ItemRequest> actualList;

        itemRequestRepository.save(itemRequest);
        itemRequestRepository.save(itemRequestTwo);

        expectedList = new ArrayList<>(List.of(itemRequest, itemRequestTwo));
        expectedList.sort(Comparator.comparing(ItemRequest::getCreated).reversed());
        actualList = itemRequestRepository.findAllByUserId(requester.getId());

        Assertions.assertEquals(expectedList, actualList);
    }

    @Test
    void findRequests() {
        List<ItemRequest> expectedList;
        List<ItemRequest> actualList;
        Pageable pageable;

        itemRequestRepository.save(itemRequest);
        itemRequestRepository.save(itemRequestTwo);

        pageable = PageRequest.of(0, 1);

        expectedList = List.of(itemRequest);
        actualList = itemRequestRepository.findRequests(pageable);

        Assertions.assertEquals(expectedList, actualList);
    }

    @Test
    void testFindRequests() {
        List<ItemRequest> expectedList;
        List<ItemRequest> actualList;
        Sort sort;

        itemRequestRepository.save(itemRequest);
        itemRequestRepository.save(itemRequestTwo);

        sort = Sort.by("id").descending();

        expectedList = List.of(itemRequestTwo, itemRequest);
        actualList = itemRequestRepository.findRequests(sort);

        Assertions.assertEquals(expectedList, actualList);
    }
}