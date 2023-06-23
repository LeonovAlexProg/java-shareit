package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface RequestService {
    ItemRequestDto addNewRequest(long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDto> getUserRequests(long userId);

    List<ItemRequestDto> getAllRequests(Long userId, Integer from, Integer size);

    ItemRequestDto getRequest(Long userId, Long requestId);
}
