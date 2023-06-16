package ru.practicum.shareit.request.mapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ItemRequestMapperTest {
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
            .user(user)
            .description("test description")
            .build();

    @Test
    void mapToDto() {
        ItemRequestDto expectedDto;
        ItemRequestDto actualDto;

        expectedDto = ItemRequestDto.builder()
                .id(itemRequest.getId())
                .items(List.of(ItemDto.of(item)))
                .description(itemRequest.getDescription())
                .created(localDateTime)
                .build();

        actualDto = ItemRequestMapper.mapToDto(itemRequest);

        Assertions.assertEquals(expectedDto, actualDto);
    }

    @Test
    void mapListToDto() {
        List<ItemRequestDto> expectedList;
        List<ItemRequestDto> actualList;

        ItemRequestDto correctDto = ItemRequestDto.builder()
                .id(itemRequest.getId())
                .items(List.of(ItemDto.of(item)))
                .description(itemRequest.getDescription())
                .created(localDateTime)
                .build();

        expectedList = List.of(correctDto, correctDto, correctDto);
        actualList = ItemRequestMapper.mapListToDto(List.of(itemRequest, itemRequest, itemRequest));

        Assertions.assertEquals(expectedList, actualList);
    }
}