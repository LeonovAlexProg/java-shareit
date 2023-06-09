package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exceptions.CommentValidationException;
import ru.practicum.shareit.item.exceptions.ItemAccessRestrictedException;
import ru.practicum.shareit.item.exceptions.ItemNotFoundException;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemRequestRepository requestRepository;

    private final BookingRepository bookingRepository;

    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public ItemDto addItem(ItemDto itemDto) {
        User user = userRepository.findById(itemDto.getUserId())
                .orElseThrow(() -> new UserNotFoundException(String.format("User id %d not found", itemDto.getUserId())));
        Item item = new Item(null, user, itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable(), null);

        if (itemDto.getRequestId() != null && requestRepository.existsById(itemDto.getRequestId())) {
            ItemRequest request = requestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new ItemRequestNotFoundException(String.format("Request id %d not found", itemDto.getRequestId())));

            item.setRequest(request);
            request.getItems().add(item);

            requestRepository.save(request);
        }

        return ItemMapper.itemDtoOf(itemRepository.save(item));
    }

    @Override
    @Transactional
    public ItemDto updateItem(ItemDto itemDto) {
        Item srcItem = Item.of(itemDto);
        Item trgItem = itemRepository.findById(itemDto.getId())
                .orElseThrow(() -> new ItemNotFoundException(String.format("Item id %d not found", itemDto.getId())));

        if (!itemDto.getUserId().equals(trgItem.getUser().getId())) {
            throw new ItemAccessRestrictedException(
                    String.format("User id %d have not access to patch Item id %d", srcItem.getUser().getId(), srcItem.getId())
            );
        }

        copyNonNullProperties(srcItem, trgItem);
        return ItemMapper.itemDtoOf(itemRepository.save(trgItem));
    }

    @Override
    public ItemDto getItem(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(String.format("Item id %d not found", itemId)));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("User id %d not found", userId)));
        List<Comment> comments = commentRepository.findAllByItemId(itemId);

        ItemDto itemDto = ItemMapper.itemDtoOf(item);

        if (item.getUser().equals(user)) {
            setLastAndNextBookingsForItem(itemDto);
        }

        itemDto.setComments(CommentMapper.listOf(comments));

        return itemDto;
    }

//    @Override
//    public List<ItemDto> getUserItems(Long userId) {
//        if (userRepository.existsById(userId)) {
//            List<ItemDto> itemDtoList = ItemMapper.listOf(itemRepository.findItemsByUserId(userId)
//                    .orElseThrow(() -> new ItemNotFoundException(String.format("User id %d have no any items", userId))));
//
//            setLastAndNextBookingsForItemList(itemDtoList);
//
//            return itemDtoList;
//        } else {
//            throw new UserNotFoundException(String.format("User id %d not found", userId));
//        }
//    }

    @Override
    public List<ItemDto> getUserItems(Long userId) {
        if (userRepository.existsById(userId)) {
            List<Item> items = itemRepository.findItemsByUserId(userId, Sort.by("id").ascending())
                    .orElseThrow(() -> new ItemNotFoundException(String.format("No items found for user $d", userId)));

            return setLastAndNextBookingsForItemList(items);
        } else {
            throw new UserNotFoundException(String.format("User id %d not found", userId));
        }
    }

    @Override
    public List<ItemDto> findItem(String text) {
        if (text.isEmpty())
            return Collections.emptyList();

        return ItemMapper.listOf(itemRepository.findItemsLike(text)
                .orElseThrow(() -> new ItemNotFoundException(String.format("No items containing %s were found", text))));
    }

    @Override
    @Transactional
    public CommentResponseDto postComment(Long userId, Long itemId, CommentRequestDto commentRequestDto) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(String.format("Item id %d not found", itemId)));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("User id %d not found", userId)));

        if (itemRepository.itemWasRentedByUser(itemId, userId, LocalDateTime.now())) {
            Comment comment = Comment.builder()
                    .item(item)
                    .user(user)
                    .text(commentRequestDto.getText())
                    .created(LocalDateTime.now())
                    .build();

            comment = commentRepository.save(comment);

            return CommentMapper.responseDtoOf(comment);
        }

        throw new CommentValidationException(String.format("User id %d can not post comments on item id %d", userId, itemId));
    }

    private void setLastAndNextBookingsForItem(ItemDto itemDto) {
        Booking lastBooking = bookingRepository
                .findLastBooking(itemDto.getId());
        Booking nextBooking = bookingRepository
                .findNextBooking(itemDto.getId());

        if (lastBooking != null) {
            itemDto.setLastBooking(BookingMapper.shortResponseDtoOf(lastBooking));
            if (nextBooking != null) {
                itemDto.setNextBooking(BookingMapper.shortResponseDtoOf(nextBooking));
            }
        }
    }

//    private void setLastAndNextBookingsForItemList(List<ItemDto> itemDtoList) {
//        itemDtoList.forEach(this::setLastAndNextBookingsForItem);
//    }

    private List<ItemDto> setLastAndNextBookingsForItemList(List<Item> items) {
        List<Long> itemsId = items.stream().map(Item::getId).collect(Collectors.toList());
        List<Booking> bookings = bookingRepository.findAllByItemsId(itemsId);

        Map<Long, List<Booking>> mappedBookings = bookings.stream().collect(Collectors.groupingBy(
                booking -> booking.getItem().getId()
        ));

        List<ItemDto> itemsDto = ItemMapper.listOf(items);

        if (!mappedBookings.isEmpty()) {
            itemsDto.forEach(itemDto -> setLastAndNextBookingsForItem(itemDto, mappedBookings));
        }

        return itemsDto;
    }

    private void setLastAndNextBookingsForItem(ItemDto itemDto, Map<Long, List<Booking>> mappedBookings) {
        LocalDateTime now = LocalDateTime.now();

        Booking lastBooking = null;
        Booking nextBooking = null;

        if (mappedBookings.containsKey(itemDto.getId())) {
            lastBooking = mappedBookings.get(itemDto.getId()).stream()
                    .sorted(Comparator.comparing(Booking::getStart).reversed())
                    .dropWhile(booking -> booking.getStart().isAfter(now))
                    .findFirst()
                    .orElse(null);
            nextBooking = mappedBookings.get(itemDto.getId()).stream()
                    .dropWhile(booking -> booking.getStart().isBefore(now))
                    .findFirst()
                    .orElse(null);
        }

        if (lastBooking != null)
            itemDto.setLastBooking(BookingMapper.shortResponseDtoOf(lastBooking));
        if (nextBooking != null)
            itemDto.setNextBooking(BookingMapper.shortResponseDtoOf(nextBooking));
    }

    private static void copyNonNullProperties(Object src, Object target) {
       BeanUtils.copyProperties(src, target, getNullPropertyNames(src));
    }

    private static String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<String>();
        for (java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) emptyNames.add(pd.getName());
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }
}
