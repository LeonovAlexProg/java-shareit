package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.exception.PaginationDataException;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class RequestServiceUnitTest {
    @InjectMocks
    RequestServiceImpl requestService;
    @Mock
    ItemRequestRepository requestRepository;
    @Mock
    UserRepository userRepository;

    User requestCreator;
    ItemRequestDto requestDto;
    ItemRequestDto responseDto;
    ItemRequest itemRequest;
    ItemRequest itemRequestSaved;

    @BeforeEach
    void setUp() {
        requestCreator = User.builder()
                .id(1L)
                .name("Zima")
                .email("zimablue@gmail.com")
                .build();

        requestDto = ItemRequestDto.builder()
                .description("I want the magic stick")
                .build();

        responseDto = ItemRequestDto.builder()
                .id(1L)
                .description("I want the magic stick")
                .created(LocalDateTime.now())
                .items(Collections.emptyList())
                .build();

        itemRequest = ItemRequest.builder()
                .created(LocalDateTime.now())
                .description(requestDto.getDescription())
                .user(requestCreator)
                .items(Collections.emptyList())
                .build();

        itemRequestSaved = ItemRequest.builder()
                .id(1L)
                .created(LocalDateTime.now())
                .description(requestDto.getDescription())
                .user(requestCreator)
                .items(Collections.emptyList())
                .build();
    }

    @Test
    void addNewRequest() {
        ItemRequestDto expectedDto;
        ItemRequestDto actualDto;

        Mockito
                .when(userRepository.findById(requestCreator.getId()))
                .thenReturn(Optional.ofNullable(requestCreator));
        Mockito
                .when(requestRepository.save(any(ItemRequest.class)))
                .thenReturn(itemRequestSaved);

        expectedDto = responseDto;
        actualDto = requestService.addNewRequest(requestCreator.getId(), requestDto);

        Assertions.assertEquals(expectedDto.getId(), actualDto.getId());
        Assertions.assertEquals(expectedDto.getDescription(), actualDto.getDescription());
        Assertions.assertEquals(expectedDto.getItems(), actualDto.getItems());
    }

    @Test
    void getRequest() {
        ItemRequestDto expectedDto;
        ItemRequestDto actualDto;

        Mockito
                .when(userRepository.existsById(requestCreator.getId()))
                .thenReturn(true);
        Mockito
                .when(requestRepository.findById(itemRequestSaved.getId()))
                .thenReturn(Optional.ofNullable(itemRequestSaved));

        expectedDto = responseDto;
        actualDto = requestService.getRequest(requestCreator.getId(), itemRequestSaved.getId());

        Assertions.assertEquals(expectedDto.getId(), actualDto.getId());
        Assertions.assertEquals(expectedDto.getDescription(), actualDto.getDescription());
        Assertions.assertEquals(expectedDto.getItems(), actualDto.getItems());
    }

    @Test
    void getUserRequests() {
        List<ItemRequestDto> expectedList;
        List<ItemRequestDto> actualList;

        Mockito
                .when(userRepository.existsById(requestCreator.getId()))
                .thenReturn(true);
        Mockito
                .when(requestRepository.findAllByUserId(requestCreator.getId()))
                .thenReturn(List.of(itemRequestSaved));

        expectedList = List.of(responseDto);
        actualList = requestService.getUserRequests(requestCreator.getId());

        Assertions.assertEquals(expectedList.get(0).getId(), actualList.get(0).getId());
        Assertions.assertEquals(expectedList.get(0).getDescription(), actualList.get(0).getDescription());
        Assertions.assertEquals(expectedList.get(0).getItems(), actualList.get(0).getItems());
    }

    @Test
    void getAllRequestsPageable() {
        List<ItemRequestDto> expectedList;
        List<ItemRequestDto> actualList;

        List<ItemRequest> items = List.of(itemRequestSaved, itemRequestSaved);

        Mockito
                .when(requestRepository.findRequests(any(Pageable.class)))
                .thenReturn(items);

        expectedList = ItemRequestMapper.mapListToDto(items);
        actualList = requestService.getAllRequests(2L, 0, 2);

        Assertions.assertEquals(expectedList, actualList);
    }

    @Test
    void getAllRequestsPageableForOwner() {
        List<ItemRequestDto> expectedList;
        List<ItemRequestDto> actualList;

        List<ItemRequest> items = List.of(itemRequestSaved, itemRequestSaved);

        Mockito
                .when(requestRepository.findRequests(any(Pageable.class)))
                .thenReturn(items);

        expectedList = Collections.emptyList();
        actualList = requestService.getAllRequests(1L, 0, 2);

        Assertions.assertEquals(expectedList, actualList);
    }

    @Test
    void getAllRequestsUnpageable() {
        List<ItemRequestDto> expectedList;
        List<ItemRequestDto> actualList;

        List<ItemRequest> items = List.of(itemRequestSaved, itemRequestSaved);

        Mockito
                .when(requestRepository.findRequests(any(Sort.class)))
                .thenReturn(items);

        expectedList = ItemRequestMapper.mapListToDto(items);
        actualList = requestService.getAllRequests(1L, null, null);

        Assertions.assertEquals(expectedList, actualList);
    }

    @Test
    void getAllRequestInvalidPagination() {
        String expectedMessage = "Invalid pagination data";
        String actualMessage;
        Exception exception;

        exception = Assertions.assertThrows(PaginationDataException.class,
                () -> requestService.getAllRequests(1L, -1, -1));
        actualMessage = exception.getMessage();

        Assertions.assertEquals(expectedMessage, actualMessage);
    }
}