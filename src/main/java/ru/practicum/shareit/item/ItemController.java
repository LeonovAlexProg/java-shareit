package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final ItemMapper mapper;

    @PostMapping
    public Item addItem(@RequestHeader("X-Sharer-User-Id") int userId,
                        @Valid @RequestBody ItemDto itemDto) {
        Item item = mapper.map(userId, itemDto);
        return itemService.addItem(item);
    }

    @PatchMapping("/{itemId}")
    public Item updateItem(@PathVariable int itemId,
                           @RequestHeader("X-Sharer-User-Id") int userId,
                           @RequestBody ItemDto itemDto) {
        Item item = mapper.map(itemId, userId, itemDto);
        return itemService.updateItem(item);
    }

    @GetMapping("/{itemId}")
    public Item getItem(@PathVariable int itemId,
                        @RequestHeader("X-Sharer-User-Id") int userId) {
        return itemService.getItem(itemId);
    }

    @GetMapping
    public List<Item> getUserItems(@RequestHeader("X-Sharer-User-Id") int userId) {
        return itemService.getUserItems(userId);
    }

    @GetMapping("/search")
    public List<Item> searchItem(@RequestParam(name = "text") String text) {
        return itemService.findItem(text);
    }
}
