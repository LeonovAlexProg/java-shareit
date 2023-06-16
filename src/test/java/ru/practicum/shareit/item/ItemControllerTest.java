package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    @Autowired
    ObjectMapper mapper;

    @MockBean
    ItemService itemService;

    @Autowired
    MockMvc mvc;

    User owner;
    ItemDto itemRequestDto;
    ItemDto itemResponseDto;

    @BeforeEach
    void setUp() {
        owner = User.builder()
                .id(1L)
                .name("zima")
                .email("zimablue@gmail.com")
                .build();

        itemRequestDto = ItemDto.builder()
                .name("drill")
                .description("drill for drilling")
                .available(true)
                .build();

        itemResponseDto = ItemDto.builder()
                .id(1L)
                .name(itemRequestDto.getName())
                .description(itemRequestDto.getDescription())
                .available(itemRequestDto.getAvailable())
                .userId(owner.getId())
                .build();
    }

    @Test
    void addItem() throws Exception{
        itemRequestDto.setUserId(owner.getId());

        Mockito
                .when(itemService.addItem(itemRequestDto))
                .thenReturn(itemResponseDto);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", String.valueOf(owner.getId()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemResponseDto.getId()))
                .andExpect(jsonPath("$.userId").value(owner.getId()))
                .andExpect(jsonPath("$.name").value(itemResponseDto.getName()));
    }

    @Test
    void updateItem() throws Exception {
        ItemDto newItemDto = ItemDto.builder()
                .id(1L)
                .name("shovel")
                .description("shovel for digging")
                .userId(owner.getId())
                .build();

        Mockito
                .when(itemService.updateItem(newItemDto))
                .thenReturn(newItemDto);

        mvc.perform(patch("/items/{itemId}", newItemDto.getId())
                .header("X-Sharer-User-Id", String.valueOf(owner.getId()))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(newItemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(newItemDto.getId()))
                .andExpect(jsonPath("$.name").value(newItemDto.getName()))
                .andExpect(jsonPath("$.description").value(newItemDto.getDescription()));
    }

    @Test
    void getItem() throws Exception {
        Mockito
                .when(itemService.getItem(itemResponseDto.getId(), itemResponseDto.getUserId()))
                .thenReturn(itemResponseDto);

        mvc.perform(get("/items/{itemId}", itemResponseDto.getId())
                        .header("X-Sharer-User-Id", String.valueOf(owner.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemResponseDto.getId()))
                .andExpect(jsonPath("$.name").value(itemResponseDto.getName()));
    }

    @Test
    void getUserItems() throws Exception {
        List<ItemDto> expectedList;

        ItemDto newItemResponseDto = ItemDto.builder()
                .id(2L)
                .name("shovel")
                .description("shovel for digging")
                .available(true)
                .userId(owner.getId())
                .build();

        expectedList = List.of(itemResponseDto, newItemResponseDto);

        Mockito
                .when(itemService.getUserItems(owner.getId()))
                .thenReturn(expectedList);

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", String.valueOf(owner.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemResponseDto.getId()))
                .andExpect(jsonPath("$[1].id").value(newItemResponseDto.getId()))
                .andExpect(jsonPath("$[0].name").value(itemResponseDto.getName()))
                .andExpect(jsonPath("$[1].name").value(newItemResponseDto.getName()));
    }

    @Test
    void searchItem() throws Exception {
        Mockito
                .when(itemService.findItem("drill"))
                .thenReturn(List.of(itemResponseDto));

        mvc.perform(get("/items/search")
                        .param("text", "drill"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemResponseDto.getId()))
                .andExpect(jsonPath("$[0].name").value(itemResponseDto.getName()));
    }

    @Test
    void postComment() throws Exception{
        CommentRequestDto commentRequestDto = CommentRequestDto.builder()
                .text("text comment")
                .build();
        CommentResponseDto commentResponseDto = CommentResponseDto.builder()
                .id(1L)
                .authorName(owner.getName())
                .text(commentRequestDto.getText())
                .build();

        Mockito
                .when(itemService.postComment(owner.getId(), itemResponseDto.getId(), commentRequestDto))
                .thenReturn(commentResponseDto);

        mvc.perform(post("/items/{itemId}/comment", itemResponseDto.getId())
                        .header("X-Sharer-User-Id", String.valueOf(owner.getId()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(commentRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentResponseDto.getId()))
                .andExpect(jsonPath("$.text").value(commentResponseDto.getText()))
                .andExpect(jsonPath("$.authorName").value(owner.getName()));
    }
}