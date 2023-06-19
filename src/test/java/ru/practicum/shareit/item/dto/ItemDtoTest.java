package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

class ItemDtoTest {
    LocalDateTime localDateTime = LocalDateTime.now();

    User user = User.builder()
            .id(1L)
            .name("zima")
            .email("zimablue@mail.ru")
            .build();
    Item item = Item.builder()
            .id(1L)
            .user(user)
            .name("drill")
            .description("drilling drill")
            .isAvailable(true)
            .build();
    ItemRequest itemRequest = ItemRequest.builder()
            .id(1L)
            .items(List.of(item))
            .created(localDateTime)
            .description("description")
            .user(user)
            .build();

    @Test
    void of() {
        ItemDto expectedDto;
        ItemDto actualDto;

        item.setRequest(itemRequest);

        expectedDto = ItemDto.builder()
                .id(1L)
                .userId(user.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(true)
                .requestId(itemRequest.getId())
                .build();

        actualDto = ItemMapper.itemDtoOf(item);

        Assertions.assertEquals(expectedDto, actualDto);
    }

    @Test
    void listOf() {
        List<ItemDto> expectedList;
        List<ItemDto> actualList;

        item.setRequest(itemRequest);

        ItemDto correctDto = ItemDto.builder()
                .id(1L)
                .userId(user.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(true)
                .requestId(itemRequest.getId())
                .build();

        expectedList = List.of(correctDto, correctDto, correctDto);
        actualList = ItemMapper.listOf(List.of(item, item, item));

        Assertions.assertEquals(expectedList, actualList);
    }
}