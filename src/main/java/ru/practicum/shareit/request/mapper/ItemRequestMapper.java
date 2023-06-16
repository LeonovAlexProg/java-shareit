package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;
import java.util.stream.Collectors;

public class ItemRequestMapper {
    public static ItemRequestDto mapToDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(ItemDto.listOf(itemRequest.getItems()))
                .build();
    }

    public static List<ItemRequestDto> mapListToDto(List<ItemRequest> itemRequests) {
        return itemRequests.stream().map(ItemRequestMapper::mapToDto).collect(Collectors.toList());
    }
}
