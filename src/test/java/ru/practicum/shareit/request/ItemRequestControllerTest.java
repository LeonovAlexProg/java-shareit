package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.RequestService;

import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {
    @Autowired
    ObjectMapper mapper;

    @MockBean
    RequestService requestService;

    @Autowired
    MockMvc mvc;

    ItemRequestDto itemRequestRequest;
    ItemRequestDto itemRequestResponse;

    @BeforeEach
    void setUp() {
        itemRequestRequest = ItemRequestDto.builder()

    }

    @Test
    void addNewItemRequest() {
    }

    @Test
    void getItemRequest() {
    }

    @Test
    void getAllUserRequests() {
    }

    @Test
    void getAllRequests() {
    }
}