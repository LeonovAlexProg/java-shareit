package ru.practicum.shareit.item.dto;

import org.springframework.stereotype.Component;

import ru.practicum.shareit.item.model.Item;

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
}
