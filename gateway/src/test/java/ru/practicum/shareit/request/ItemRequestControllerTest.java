package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {
    @Autowired
    ObjectMapper mapper;

    @MockBean
    ItemRequestClient itemRequestClient;

    @Autowired
    MockMvc mvc;

    UserDto requesterDto;
    ItemRequestDto itemRequestRequest;
    ItemRequestDto itemRequestResponse;

    @BeforeEach
    void setUp() {
        LocalDateTime localDateTime = LocalDateTime.now();

        requesterDto = UserDto.builder()
                .name("Zima Blue")
                .email("zimablue@gmail.com")
                .id(2L)
                .build();

        itemRequestRequest = ItemRequestDto.builder()
                .description("i need the motivation")
                .build();

        itemRequestResponse = ItemRequestDto.builder()
                .id(1L)
                .created(localDateTime)
                .description(itemRequestRequest.getDescription())
                .build();
    }

    @Test
    void addNewItemRequest() throws Exception {
        ResponseEntity<Object> responseEntity = ResponseEntity.ok(mapper.writeValueAsString(itemRequestResponse));

        Mockito
                .when(itemRequestClient.addNewRequest(requesterDto.getId(), itemRequestRequest))
                .thenReturn(responseEntity);

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", String.valueOf(requesterDto.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(mapper.writeValueAsString(itemRequestRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemRequestResponse.getId()))
                .andExpect(jsonPath("$.description").value(itemRequestResponse.getDescription()));
    }

    @Test
    void getItemRequest() throws Exception {
        ResponseEntity<Object> responseEntity = ResponseEntity.ok(mapper.writeValueAsString(itemRequestResponse));

        Mockito
                .when(itemRequestClient.getRequest(requesterDto.getId(), itemRequestResponse.getId()))
                .thenReturn(responseEntity);

        mvc.perform(get("/requests/{requestId}", itemRequestResponse.getId())
                        .header("X-Sharer-User-Id", String.valueOf(requesterDto.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemRequestResponse.getId()))
                .andExpect(jsonPath("$.description").value(itemRequestResponse.getDescription()));
    }

    @Test
    void getAllUserRequests() throws Exception {
        List<ItemRequestDto> expectedList;

        ItemRequestDto newItemRequestResponse = ItemRequestDto.builder()
                .id(2L)
                .created(LocalDateTime.now())
                .description("new description")
                .build();

        expectedList = List.of(itemRequestResponse, newItemRequestResponse);

        ResponseEntity<Object> responseEntity = ResponseEntity.ok(mapper.writeValueAsString(expectedList));

        Mockito
                .when(itemRequestClient.getUserRequests(requesterDto.getId()))
                .thenReturn(responseEntity);

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", String.valueOf(requesterDto.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemRequestResponse.getId()))
                .andExpect(jsonPath("$[1].id").value(newItemRequestResponse.getId()))
                .andExpect(jsonPath("$[0].description").value(itemRequestResponse.getDescription()))
                .andExpect(jsonPath("$[1].description").value(newItemRequestResponse.getDescription()));
    }

    @Test
    void getAllRequests() throws Exception {
        List<ItemRequestDto> expectedList;

        expectedList = List.of(itemRequestResponse);

        ResponseEntity<Object> responseEntity = ResponseEntity.ok(mapper.writeValueAsString(expectedList));

        Mockito
                .when(itemRequestClient.getAllRequests(requesterDto.getId(), 0, 1))
                .thenReturn(responseEntity);

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", String.valueOf(requesterDto.getId()))
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(itemRequestResponse.getId()));
    }
}