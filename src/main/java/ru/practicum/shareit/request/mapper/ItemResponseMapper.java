package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemResponseDto;

import java.util.List;
import java.util.stream.Collectors;

public class ItemResponseMapper {
    public static ItemResponseDto mapToDto(Item item) {
        return ItemResponseDto.builder()
                .id(item.getId())
                .name(item.getName())
                .ownerId(item.getUser().getId())
                .build();
    }

    public static List<ItemResponseDto> mapListToDto(List<Item> items) {
        return items.stream().map(ItemResponseMapper::mapToDto).collect(Collectors.toList());
    }
}
