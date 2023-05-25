package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.constraints.ItemCreateConstraint;
import ru.practicum.shareit.item.constraints.ItemIdConstraint;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final ItemMapper mapper;

    @PostMapping
    public ItemDto addItem(@RequestHeader("X-Sharer-User-Id") int userId,
                        @Validated({ItemIdConstraint.class,
                                ItemCreateConstraint.class}) @RequestBody ItemDto itemDto) {
        Item item = mapper.map(userId, itemDto);
        return mapper.map(itemService.addItem(item));
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable int itemId,
                           @RequestHeader("X-Sharer-User-Id") int userId,
                           @Valid @RequestBody ItemDto itemDto) {
        Item item = mapper.map(itemId, userId, itemDto);
        return mapper.map(itemService.updateItem(item));
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable int itemId,
                        @RequestHeader("X-Sharer-User-Id") int userId) {
        return mapper.map(itemService.getItem(itemId));
    }

    @GetMapping
    public List<ItemDto> getUserItems(@RequestHeader("X-Sharer-User-Id") int userId) {
        return mapper.map(itemService.getUserItems(userId));
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam(name = "text") String text) {
        return mapper.map(itemService.findItem(text));
    }
}
