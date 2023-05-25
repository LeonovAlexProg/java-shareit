package ru.practicum.shareit.item.dto;

import org.springframework.stereotype.Component;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ItemMapper {
    public Item map(int userId, ItemDto itemDto) {
        return Item.builder()
                .ownerId(userId)
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .build();
    }

    public Item map(int itemId, int userId, ItemDto itemDto) {
        return Item.builder()
                .id(itemId)
                .ownerId(userId)
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .build();
    }

    public ItemDto map(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    public List<ItemDto> map(List<Item> items) {
        return items.stream().map(this::map).collect(Collectors.toList());
    }
}
