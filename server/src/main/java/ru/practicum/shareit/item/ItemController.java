package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.constraints.ItemCreateConstraint;
import ru.practicum.shareit.item.constraints.ItemIdConstraint;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto addItem(@RequestHeader("X-Sharer-User-Id") long userId,
                               @Validated({ItemIdConstraint.class, ItemCreateConstraint.class}) @Valid @RequestBody ItemDto itemUserDto) {
        log.info("Creating item name {}", itemUserDto.getName());
        itemUserDto.setUserId(userId);
        return itemService.addItem(itemUserDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable long itemId,
                              @RequestHeader("X-Sharer-User-Id") long userId,
                              @Valid @RequestBody ItemDto itemUserDto) {
        log.info("Updating item id {}", itemId);
        itemUserDto.setId(itemId);
        itemUserDto.setUserId(userId);
        return itemService.updateItem(itemUserDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable long itemId,
                               @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Getting item id {}", itemId);
        return itemService.getItem(itemId, userId);
    }

    @GetMapping
    public List<ItemDto> getUserItems(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Getting all user item userid {}", userId);
        return itemService.getUserItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                    @RequestParam(name = "text") String text) {
        log.info("Searching for item with {}", text);
        return itemService.findItem(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentResponseDto postComment(@RequestHeader(name = "X-Sharer-User-Id") long userId,
                                          @PathVariable long itemId,
                                          @Valid @RequestBody CommentRequestDto commentRequestDto) {
        log.info("Posting comment from user id {} to item id {}", userId, itemId);
        return itemService.postComment(userId, itemId, commentRequestDto);
    }
}
