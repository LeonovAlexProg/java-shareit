package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.constraints.ItemCreateConstraint;
import ru.practicum.shareit.item.constraints.ItemIdConstraint;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                  @Validated({ItemIdConstraint.class, ItemCreateConstraint.class}) @Valid @RequestBody ItemDto itemUserDto) {
        log.info("Create new item userid {} name {}", userId, itemUserDto.getName());
        return itemClient.addItem(userId, itemUserDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@PathVariable long itemId,
                              @RequestHeader("X-Sharer-User-Id") long userId,
                              @Valid @RequestBody ItemDto itemUserDto) {
        log.info("Update item id {}", itemId);
        return itemClient.updateItem(userId, itemId, itemUserDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@PathVariable long itemId,
                           @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Get item id {}", itemId);
        return itemClient.getItem(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserItems(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Get user {} items", userId);
        return itemClient.getUserItems(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @RequestParam(name = "text") String text) {
        log.info("Search item text {}", text);
        return itemClient.findItem(userId, text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> postComment(@RequestHeader(name = "X-Sharer-User-Id") long userId,
                                          @PathVariable long itemId,
                                          @Valid @RequestBody CommentRequestDto commentRequestDto) {
        log.info("Post comment userid {} itemid {}", userId, itemId);
        return itemClient.postComment(userId, itemId, commentRequestDto);
    }
}