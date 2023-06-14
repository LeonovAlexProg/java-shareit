package ru.practicum.shareit.item.service;

import org.checkerframework.checker.nullness.Opt;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplUnitTest {
    @InjectMocks
    ItemServiceImpl itemService;

    @Mock
    ItemRepository itemRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    ItemRequestRepository requestRepository;
    @Mock
    BookingRepository bookingRepository;
    @Mock
    CommentRepository commentRepository;

    ItemDto itemDto;
    ItemDto itemDtoWithRequest;
    User owner;
    User requester;
    Item item;
    Item itemSaved;
    Item itemWithRequest;
    Item itemWithRequestSaved;
    ItemRequest request;
    ItemRequest requestSaved;
    ItemRequest requestWithItem;
    ItemRequest requestWithItemSaved;


    @BeforeEach
    void init() {
        itemDto = ItemDto.builder()
                .id(1L)
                .name("Shovel")
                .description("Shovel for digging what is digging")
                .userId(1L)
                .available(true)
                .build();

        itemDtoWithRequest = ItemDto.builder()
                .id(1L)
                .name("Shovel")
                .description("Shovel for digging what is digging")
                .userId(1L)
                .available(true)
                .requestId(1L)
                .build();

        owner = User.builder()
                .id(1L)
                .name("Zima")
                .email("zimablue@gmail.com")
                .build();

        requester = User.builder()
                .id(2L)
                .name("Alex")
                .email("alex@gmail.com")
                .build();

        item = Item.builder()
                .name("Shovel")
                .user(owner)
                .description("Shovel for digging what is digging")
                .isAvailable(true)
                .build();

        itemSaved = Item.builder()
                .id(1L)
                .name("Shovel")
                .user(owner)
                .description("Shovel for digging what is digging")
                .isAvailable(true)
                .build();

        itemWithRequest = Item.builder()
                .name("Shovel")
                .user(owner)
                .description("Shovel for digging what is digging")
                .isAvailable(true)
                .build();

        itemWithRequestSaved = Item.builder()
                .id(1L)
                .name("Shovel")
                .user(owner)
                .description("Shovel for digging what is digging")
                .isAvailable(true)

                .build();

        request = ItemRequest.builder()
                .description("test description")
                .created(LocalDateTime.now())
                .user(requester)
                .items(new ArrayList<>())
                .build();

        requestSaved = ItemRequest.builder()
                .id(1L)
                .description("test description")
                .created(LocalDateTime.now())
                .user(requester)
                .items(new ArrayList<>())
                .build();

        requestWithItem = ItemRequest.builder()
                .description("test description")
                .created(LocalDateTime.now())
                .user(requester)
                .items(new ArrayList<>(List.of(itemWithRequest)))
                .build();

        requestWithItemSaved = ItemRequest.builder()
                        .id(1L)
                        .description("test description")
                        .created(LocalDateTime.now())
                        .user(requester)
                        .items(new ArrayList<>(List.of(itemWithRequest)))
                        .build();

        itemWithRequest.setRequest(requestSaved);
        itemWithRequestSaved.setRequest(requestSaved);
    }


    @Test
    void addItemWithoutRequest() {
        ItemDto expectedDto;
        ItemDto actualDto;

        Mockito
                .when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.ofNullable(owner));
        Mockito
                .when(itemRepository.save(item))
                .thenReturn(itemSaved);

        expectedDto = itemDto;
        actualDto = itemService.addItem(expectedDto);

        Assertions.assertEquals(expectedDto, actualDto);
    }

    @Test
    void addItemWithRequest() {
        ItemDto expectedDto;
        ItemDto actualDto;

        Mockito
                .when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.ofNullable(owner));
        Mockito
                .when(requestRepository.existsById(requestSaved.getId()))
                .thenReturn(true);
        Mockito
                .when(requestRepository.findById(requestSaved.getId()))
                .thenReturn(Optional.ofNullable(requestSaved));
        Mockito
                .verify(requestRepository, Mockito.times(0))
                .save(any(ItemRequest.class));
        Mockito
                .when(itemRepository.save(itemWithRequest))
                .thenReturn(itemWithRequestSaved);


        expectedDto = itemDtoWithRequest;
        actualDto = itemService.addItem(expectedDto);

        Assertions.assertEquals(expectedDto, actualDto);
    }



    @Test
    void updateItem() {
        ItemDto expectedDto;
        ItemDto actualDto;

        ItemDto updatedDto = ItemDto.builder()
                .id(1L)
                .name("Drill")
                .description("Drill for drilling drillable things")
                .userId(1L)
                .available(true)
                .build();
        Item updatedItem = Item.builder()
                .id(1L)
                .name("Drill")
                .user(owner)
                .description("Drill for drilling drillable things")
                .isAvailable(true)
                .build();

        Mockito
                .when(itemRepository.findById(itemSaved.getId()))
                .thenReturn(Optional.ofNullable(itemSaved));
        Mockito
                .when(itemRepository.save(updatedItem))
                .thenReturn(updatedItem);

        expectedDto = updatedDto;
        actualDto = itemService.updateItem(updatedDto);

        Assertions.assertEquals(expectedDto, actualDto);
    }

    @Test
    void getItemWithoutComments() {
        ItemDto expectedDto;
        ItemDto actualDto;

        Mockito
                .when(itemRepository.findById(itemSaved.getId()))
                .thenReturn(Optional.ofNullable(itemSaved));
        Mockito
                .when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.ofNullable(owner));

    }

    @Test
    void getUserItems() {
    }

    @Test
    void findItem() {
    }

    @Test
    void postComment() {
    }
}