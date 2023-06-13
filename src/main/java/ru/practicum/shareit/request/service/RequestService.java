package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface RequestService {
    ItemRequestDto addNewRequest(long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDto> getUserRequests(long userId);

    List<ItemRequestDto> getAllRequests(Integer from, Integer size);

    ItemRequestDto getRequest(long requestId);
}
