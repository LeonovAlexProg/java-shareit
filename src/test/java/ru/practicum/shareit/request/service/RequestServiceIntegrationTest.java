package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class RequestServiceIntegrationTest {
    private final RequestService requestService;

    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;

    User requester;
    ItemRequest request;

    LocalDateTime localDateTime;

    @BeforeEach
    void setUp() {
        localDateTime = LocalDateTime.now();

        requester = User.builder()
                .name("zima")
                .email("zimablue@gmail.com")
                .build();

        request = ItemRequest.builder()
                .description("test description")
                .created(localDateTime)
                .user(requester)
                .items(new ArrayList<>())
                .build();
    }

    @Test
    void addNewRequest() {
        ItemRequestDto actualDto;

        User requesterSaved = userRepository.save(requester);

        ItemRequestDto request = ItemRequestDto.builder()
                .description("i want superman's red cape")
                .build();

        actualDto = requestService.addNewRequest(requesterSaved.getId(), request);

        Assertions.assertEquals(1L, actualDto.getId());
        Assertions.assertEquals(request.getDescription(), actualDto.getDescription());
        Assertions.assertEquals(Collections.emptyList(), actualDto.getItems());
    }

    @Test
    void getRequest() {
        ItemRequestDto expectedDto;
        ItemRequestDto actualDto;

        User requesterSaved = userRepository.save(requester);
        request.setUser(requesterSaved);

        ItemRequest requestSaved = requestRepository.save(request);

        expectedDto = ItemRequestMapper.mapToDto(requestSaved);
        actualDto = requestService.getRequest(requester.getId(), requestSaved.getId());

        Assertions.assertEquals(expectedDto, actualDto);
    }

    @Test
    void getUserRequests() {
        List<ItemRequestDto> expectedList;
        List<ItemRequestDto> actualList;

        User requesterSaved = userRepository.save(requester);
        request.setUser(requesterSaved);

        ItemRequest requestTwo = ItemRequest.builder()
                .description("different description")
                .created(localDateTime)
                .user(requester)
                .items(new ArrayList<>())
                .build();

        ItemRequest requestOneSaved = requestRepository.save(request);
        ItemRequest requestTwoSaved = requestRepository.save(requestTwo);

        expectedList = ItemRequestMapper.mapListToDto(List.of(requestOneSaved, requestTwoSaved));
        actualList = requestService.getUserRequests(requesterSaved.getId());

        Assertions.assertEquals(expectedList, actualList);
    }

    @Test
    void getAllRequests() {
        List<ItemRequestDto> expectedList;
        List<ItemRequestDto> actualList;

        User requesterSaved = userRepository.save(requester);
        request.setUser(requesterSaved);

        ItemRequest requestTwo = ItemRequest.builder()
                .description("different description")
                .created(localDateTime)
                .user(requester)
                .items(new ArrayList<>())
                .build();

        ItemRequest requestOneSaved = requestRepository.save(request);
        ItemRequest requestTwoSaved = requestRepository.save(requestTwo);

        expectedList = Collections.emptyList();
        actualList = requestService.getAllRequests(requester.getId(), 0, 10);

        Assertions.assertEquals(expectedList, actualList);
    }
}