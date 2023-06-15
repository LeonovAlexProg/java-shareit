package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
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
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemServiceIntegrationTest {
    private final ItemService itemService;

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemRequestRepository requestRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    ItemRequest request;
    ItemDto itemDto;
    User owner;
    User requester;

    LocalDateTime localDateTime;

    @BeforeEach
    void setUp() {
        localDateTime = LocalDateTime.now();

        owner = User.builder()
                .name("zima")
                .email("zimablue@gmail.com")
                .build();

        requester = User.builder()
                .name("miscuzi")
                .email("miscuzimiscuzi@gmail.com")
                .build();

        request = ItemRequest.builder()
                .description("test description")
                .created(localDateTime)
                .user(requester)
                .items(new ArrayList<>())
                .build();

        itemDto = ItemDto.builder()
                .name("drill")
                .description("drilly-drilly-drill")
                .available(true)
                .build();
    }

    @Test
    void addItem() {
        ItemDto actualItemDto;

        UserDto ownerDto = UserDto.of(userRepository.save(owner));
        UserDto requesterDto = UserDto.of(userRepository.save(requester));
        Long itemRequestId = requestRepository.save(request).getId();
        itemDto.setUserId(ownerDto.getId());
        itemDto.setRequestId(itemRequestId);



        actualItemDto = itemService.addItem(itemDto);

        Assertions.assertEquals(1, actualItemDto.getId());
        Assertions.assertEquals(ownerDto.getId(), actualItemDto.getUserId());
        Assertions.assertEquals(itemDto.getName(), actualItemDto.getName());
        Assertions.assertEquals(itemDto.getDescription(), actualItemDto.getDescription());
        Assertions.assertEquals(Boolean.TRUE, actualItemDto.getAvailable());
        Assertions.assertEquals(itemDto.getRequestId(), actualItemDto.getRequestId());
    }

    @Test
    void updateItem() {
        ItemDto actualItemDto;
        Item trgItem;
        Item srcItem;

        User ownerSaved = userRepository.save(owner);

        trgItem = Item.builder()
                .name("shovel")
                .description("shovelly-shovelly-knight!!")
                .user(ownerSaved)
                .isAvailable(true)
                .build();
        Long trgItemId = itemRepository.save(trgItem).getId();

        itemDto.setId(trgItemId);
        itemDto.setUserId(ownerSaved.getId());

        actualItemDto = itemService.updateItem(itemDto);

        Assertions.assertEquals(trgItem.getId(), actualItemDto.getId());
        Assertions.assertEquals(ownerSaved.getId(), actualItemDto.getUserId());
        Assertions.assertEquals(itemDto.getName(), actualItemDto.getName());
        Assertions.assertEquals(itemDto.getDescription(), actualItemDto.getDescription());
        Assertions.assertEquals(Boolean.TRUE, actualItemDto.getAvailable());
        Assertions.assertEquals(itemDto.getRequestId(), actualItemDto.getRequestId());
    }

    @Test
    void getItem() {
        ItemDto actualItemDto;

        User ownerSaved = userRepository.save(owner);
        User commenter = userRepository.save(requester);

        Item item = Item.builder()
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .isAvailable(itemDto.getAvailable())
                .user(ownerSaved)
                .build();

        Item itemSaved = itemRepository.save(item);

        Comment comment = Comment.builder()
                .item(itemSaved)
                .user(commenter)
                .text("commenting item")
                .created(localDateTime)
                .build();

        Comment commentSaved = commentRepository.save(comment);

        actualItemDto = itemService.getItem(itemSaved.getId(), ownerSaved.getId());

        Assertions.assertEquals(itemSaved.getId(), actualItemDto.getId());
        Assertions.assertEquals(itemSaved.getName(), actualItemDto.getName());
        Assertions.assertEquals(CommentResponseDto.listOf(List.of(commentSaved)), actualItemDto.getComments());
    }

    @Test
    void getUserItems() {
        List<ItemDto> actualItemDtoList;

        User ownerSaved = userRepository.save(owner);
        User bookerSaved = userRepository.save(requester);
        Item itemOne = Item.builder()
                .name("drill")
                .description("drilling drill")
                .user(ownerSaved)
                .isAvailable(true)
                .build();
        Item itemTwo = Item.builder()
                .name("shovel")
                .description("shovelling-shovel")
                .user(ownerSaved)
                .isAvailable(true)
                .build();
        Item itemOneSaved = itemRepository.save(itemOne);
        Item itemTwoSaved = itemRepository.save(itemTwo);

        Booking lastBookingOne = Booking.builder()
                .item(itemOneSaved)
                .start(localDateTime.minusDays(10))
                .end(localDateTime.minusDays(9))
                .status(Booking.Status.APPROVED)
                .user(bookerSaved)
                .build();
        Booking lastBookingTwo = Booking.builder()
                .item(itemTwoSaved)
                .start(localDateTime.minusDays(2))
                .end(localDateTime.minusDays(1))
                .status(Booking.Status.APPROVED)
                .user(bookerSaved)
                .build();
        Booking nextBookingOne = Booking.builder()
                .item(itemOneSaved)
                .start(localDateTime.plusDays(9))
                .end(localDateTime.plusDays(10))
                .status(Booking.Status.APPROVED)
                .user(bookerSaved)
                .build();
        Booking nextBookingTwo = Booking.builder()
                .item(itemTwoSaved)
                .start(localDateTime.plusDays(1))
                .end(localDateTime.plusDays(2))
                .status(Booking.Status.APPROVED)
                .user(bookerSaved)
                .build();

        BookingShortDto lastBookingOneDto = BookingShortDto.of(bookingRepository.save(lastBookingOne));
        BookingShortDto lastBookingTwoDto = BookingShortDto.of(bookingRepository.save(lastBookingTwo));
        BookingShortDto nextBookingOneDto = BookingShortDto.of(bookingRepository.save(nextBookingOne));
        BookingShortDto nextBookingTwoDto = BookingShortDto.of(bookingRepository.save(nextBookingTwo));

        actualItemDtoList = itemService.getUserItems(ownerSaved.getId());

        Assertions.assertEquals(lastBookingOneDto, actualItemDtoList.get(0).getLastBooking());
        Assertions.assertEquals(lastBookingTwoDto, actualItemDtoList.get(1).getLastBooking());
        Assertions.assertEquals(nextBookingOneDto, actualItemDtoList.get(0).getNextBooking());
        Assertions.assertEquals(nextBookingTwoDto, actualItemDtoList.get(1).getNextBooking());
    }

    @Test
    void findItem() {
        List<ItemDto> expectedDtoList;
        List<ItemDto> actualDtoList;

        User ownerSaved = userRepository.save(owner);

        Item item = Item.builder()
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .isAvailable(itemDto.getAvailable())
                .user(ownerSaved)
                .build();

        Item itemSaved = itemRepository.save(item);

        expectedDtoList = List.of(ItemDto.of(itemSaved));
        actualDtoList = itemService.findItem("drill");

        Assertions.assertEquals(expectedDtoList, actualDtoList);
    }

    @Test
    void postComment() {
        CommentResponseDto actualCommentDto;

        User ownerSaved = userRepository.save(owner);
        User bookerSaved = userRepository.save(requester);

        Item item = Item.builder()
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .isAvailable(itemDto.getAvailable())
                .user(ownerSaved)
                .build();
        Item itemSaved = itemRepository.save(item);

        Booking booking = Booking.builder()
                .item(itemSaved)
                .start(localDateTime.minusDays(4))
                .end(localDateTime.minusDays(2))
                .status(Booking.Status.APPROVED)
                .user(bookerSaved)
                .build();
        Booking bookingSaved = bookingRepository.save(booking);

        CommentRequestDto commentRequestDto = CommentRequestDto.builder()
                .text("foobar")
                .build();

        actualCommentDto = itemService.postComment(bookerSaved.getId(), itemSaved.getId(), commentRequestDto);

        Assertions.assertEquals(1L, actualCommentDto.getId());
        Assertions.assertEquals(commentRequestDto.getText(), actualCommentDto.getText());
        Assertions.assertEquals(bookerSaved.getName(), actualCommentDto.getAuthorName());
    }
}