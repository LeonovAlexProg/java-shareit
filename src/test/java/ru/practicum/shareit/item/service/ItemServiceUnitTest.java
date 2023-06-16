package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
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

import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceUnitTest {
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
    ItemDto itemDtoWithBookings;
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
    Booking lastBooking;
    Booking nextBooking;
    Comment comment;
    Comment commentSaved;
    CommentResponseDto commentResponseDto;
    CommentRequestDto commentRequestDto;


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

        lastBooking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().minusDays(4))
                .end(LocalDateTime.now().minusDays(3))
                .user(requester)
                .item(itemSaved)
                .status(Booking.Status.APPROVED)
                .build();

        nextBooking = Booking.builder()
                .id(2L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .user(requester)
                .item(itemSaved)
                .status(Booking.Status.APPROVED)
                .build();

        itemDtoWithBookings = ItemDto.builder()
                .id(1L)
                .name("Shovel")
                .description("Shovel for digging what is digging")
                .userId(1L)
                .available(true)
                .lastBooking(BookingShortDto.of(lastBooking))
                .nextBooking(BookingShortDto.of(nextBooking))
                .build();

        comment = Comment.builder()
                .item(itemSaved)
                .user(requester)
                .text("comment text")
                .created(LocalDateTime.of(2000, 12, 12, 12, 12))
                .build();

        commentSaved = Comment.builder()
                .id(1L)
                .item(itemSaved)
                .user(requester)
                .text("comment text")
                .created(LocalDateTime.of(2000, 12, 12, 12, 12))
                .build();

        commentResponseDto = CommentResponseDto.builder()
                .id(1L)
                .text("comment text")
                .authorName(requester.getName())
                .created(comment.getCreated())
                .build();

        commentRequestDto = CommentRequestDto.builder()
                .text("comment text")
                .build();
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
    void getItemWithoutCommentsAndBookings() {
        ItemDto expectedDto;
        ItemDto actualDto;

        itemDto.setComments(Collections.emptyList());
        Mockito
                .when(itemRepository.findById(itemSaved.getId()))
                .thenReturn(Optional.ofNullable(itemSaved));
        Mockito
                .when(userRepository.findById(requester.getId()))
                .thenReturn(Optional.ofNullable(requester));
        Mockito
                .when(commentRepository.findAllByItemId(itemSaved.getId()))
                .thenReturn(Collections.emptyList());

        expectedDto = itemDto;
        actualDto = itemService.getItem(itemSaved.getId(), requester.getId());

        Assertions.assertEquals(expectedDto, actualDto);
    }

    @Test
    void getItemWithCommentsAndBookings() {
        ItemDto expectedDto;
        ItemDto actualDto;

        itemDtoWithBookings.setComments(Collections.emptyList());
        Mockito
                .when(itemRepository.findById(itemSaved.getId()))
                .thenReturn(Optional.ofNullable(itemSaved));
        Mockito
                .when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.ofNullable(owner));
        Mockito
                .when(commentRepository.findAllByItemId(itemSaved.getId()))
                .thenReturn(Collections.emptyList());
        Mockito
                .when(bookingRepository.findLastBooking(itemDtoWithBookings.getId()))
                .thenReturn(lastBooking);
        Mockito
                .when(bookingRepository.findNextBooking(itemDtoWithBookings.getId()))
                .thenReturn(nextBooking);

        expectedDto = itemDtoWithBookings;
        actualDto = itemService.getItem(itemSaved.getId(), owner.getId());

        Assertions.assertEquals(expectedDto, actualDto);
    }

    @Test
    void getUserItems() {
        List<ItemDto> expectedList;
        List<ItemDto> actualList;

        Mockito
                .when(userRepository.existsById(owner.getId()))
                .thenReturn(true);
        Mockito
                .when(itemRepository.findItemsByUserId(owner.getId()))
                .thenReturn(Optional.of(List.of(itemSaved)));
        Mockito
                .when(bookingRepository.findLastBooking(itemDtoWithBookings.getId()))
                .thenReturn(lastBooking);
        Mockito
                .when(bookingRepository.findNextBooking(itemDtoWithBookings.getId()))
                .thenReturn(nextBooking);

        expectedList = List.of(itemDtoWithBookings);
        actualList = itemService.getUserItems(owner.getId());

        Assertions.assertEquals(expectedList, actualList);
    }

    @Test
    void findItem() {
        List<ItemDto> expectedList;
        List<ItemDto> actualList;

        Mockito
                .when(itemRepository.findItemsLike(anyString()))
                .thenAnswer(invocationOnMock -> {
                    String text = invocationOnMock.getArgument(0, String.class);
                    if (text.equals("drill")) {
                        return Optional.of(List.of(itemSaved));
                    } else if (text.isEmpty()) {
                        return Optional.of(Collections.emptyList());
                    }
                    return null;
                });


        expectedList = List.of(itemDto);
        actualList = itemService.findItem("drill");

        Assertions.assertEquals(expectedList, actualList);

        expectedList = Collections.emptyList();
        actualList = itemService.findItem("");

        Assertions.assertEquals(expectedList, actualList);
    }

    @Test
    void postComment() {
        CommentResponseDto expectedComment;
        CommentResponseDto actualComment;

        Mockito
                .when(itemRepository.findById(itemSaved.getId()))
                .thenReturn(Optional.ofNullable(itemSaved));
        Mockito
                .when(userRepository.findById(requester.getId()))
                .thenReturn(Optional.ofNullable(requester));
        Mockito
                .when(itemRepository.itemWasRentedByUser(anyLong(), anyLong(), any(LocalDateTime.class)))
                .thenReturn(true);
        Mockito
                .when(commentRepository.save(any(Comment.class)))
                .thenReturn(commentSaved);

        expectedComment = commentResponseDto;
        actualComment = itemService.postComment(requester.getId(), itemSaved.getId(), commentRequestDto);

        Assertions.assertEquals(expectedComment, actualComment);
    }
}