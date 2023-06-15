package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class RequestServiceImplTest {
    private final RequestService requestService;

    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;



    @Test
    void addNewRequest() {
    }

    @Test
    void getRequest() {
    }

    @Test
    void getUserRequests() {
    }

    @Test
    void getAllRequests() {
    }
}