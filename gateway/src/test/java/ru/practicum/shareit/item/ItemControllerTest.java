package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

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
    ItemClient itemClient;

    @Autowired
    MockMvc mvc;

    UserDto ownerDto;
    ItemDto itemRequestDto;
    ItemDto itemResponseDto;

    @BeforeEach
    void setUp() {
        ownerDto = UserDto.builder()
                .name("Zima Blue")
                .email("zimablue@gmail.com")
                .id(2L)
                .build();

        itemRequestDto = ItemDto.builder()
                .name("drill")
                .description("drill for drilling")
                .userId(ownerDto.getId())
                .available(true)
                .build();

        itemResponseDto = ItemDto.builder()
                .id(1L)
                .name(itemRequestDto.getName())
                .description(itemRequestDto.getDescription())
                .available(itemRequestDto.getAvailable())
                .userId(ownerDto.getId())
                .build();
    }

    @Test
    void addItem() throws Exception {
        ResponseEntity<Object> responseEntity = ResponseEntity.ok(mapper.writeValueAsString(itemResponseDto));

        Mockito
                .when(itemClient.addItem(ownerDto.getId(), itemRequestDto))
                .thenReturn(responseEntity);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", String.valueOf(ownerDto.getId()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemResponseDto.getId()))
                .andExpect(jsonPath("$.userId").value(ownerDto.getId()))
                .andExpect(jsonPath("$.name").value(itemResponseDto.getName()));
    }

    @Test
    void updateItem() throws Exception {
        ItemDto newItemDto = ItemDto.builder()
                .id(1L)
                .name("shovel")
                .description("shovel for digging")
                .userId(ownerDto.getId())
                .build();

        ResponseEntity<Object> responseEntity = ResponseEntity.ok(mapper.writeValueAsString(newItemDto));

        Mockito
                .when(itemClient.updateItem(ownerDto.getId(), newItemDto.getId(), newItemDto))
                .thenReturn(responseEntity);

        mvc.perform(patch("/items/{itemId}", newItemDto.getId())
                        .header("X-Sharer-User-Id", String.valueOf(ownerDto.getId()))
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
        ResponseEntity<Object> responseEntity = ResponseEntity.ok(mapper.writeValueAsString(itemResponseDto));

        Mockito
                .when(itemClient.getItem(itemResponseDto.getId(), itemResponseDto.getUserId()))
                .thenReturn(responseEntity);

        mvc.perform(get("/items/{itemId}", itemResponseDto.getId())
                        .header("X-Sharer-User-Id", String.valueOf(ownerDto.getId())))
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
                .userId(ownerDto.getId())
                .build();

        expectedList = List.of(itemResponseDto, newItemResponseDto);

        ResponseEntity<Object> responseEntity = ResponseEntity.ok(mapper.writeValueAsString(expectedList));

        Mockito
                .when(itemClient.getUserItems(ownerDto.getId()))
                .thenReturn(responseEntity);

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", String.valueOf(ownerDto.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemResponseDto.getId()))
                .andExpect(jsonPath("$[1].id").value(newItemResponseDto.getId()))
                .andExpect(jsonPath("$[0].name").value(itemResponseDto.getName()))
                .andExpect(jsonPath("$[1].name").value(newItemResponseDto.getName()));
    }

    @Test
    void searchItem() throws Exception {
        ResponseEntity<Object> responseEntity = ResponseEntity.ok(mapper.writeValueAsString(List.of(itemResponseDto)));

        Mockito
                .when(itemClient.findItem(ownerDto.getId(), "drill"))
                .thenReturn(responseEntity);

        mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", String.valueOf(ownerDto.getId()))
                        .param("text", "drill"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemResponseDto.getId()))
                .andExpect(jsonPath("$[0].name").value(itemResponseDto.getName()));
    }

    @Test
    void postComment() throws Exception {
        CommentRequestDto commentRequestDto = CommentRequestDto.builder()
                .text("text comment")
                .build();
        CommentResponseDto commentResponseDto = CommentResponseDto.builder()
                .id(1L)
                .authorName(ownerDto.getName())
                .text(commentRequestDto.getText())
                .build();

        ResponseEntity<Object> responseEntity = ResponseEntity.ok(mapper.writeValueAsString(commentResponseDto));

        Mockito
                .when(itemClient.postComment(ownerDto.getId(), itemResponseDto.getId(), commentRequestDto))
                .thenReturn(responseEntity);

        mvc.perform(post("/items/{itemId}/comment", itemResponseDto.getId())
                        .header("X-Sharer-User-Id", String.valueOf(ownerDto.getId()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(commentRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentResponseDto.getId()))
                .andExpect(jsonPath("$.text").value(commentResponseDto.getText()))
                .andExpect(jsonPath("$.authorName").value(ownerDto.getName()));
    }
}