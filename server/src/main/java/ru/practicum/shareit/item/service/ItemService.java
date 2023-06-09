package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto addItem(ItemDto itemUserDto);

    ItemDto updateItem(ItemDto itemUserDto);

    ItemDto getItem(Long itemId, Long userId);

    List<ItemDto> getUserItems(Long userId);

    List<ItemDto> findItem(String text);

    CommentResponseDto postComment(Long userId, Long itemId, CommentRequestDto commentRequestDto);
}
