package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {
    private final ItemRequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> addNewItemRequest(@RequestHeader(value = "X-Sharer-User-Id") long userId,
                                            @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Create new item request by user id {}", userId);
        return requestClient.addNewRequest(userId, itemRequestDto);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequest(@RequestHeader(value = "X-Sharer-User-Id") long userId,
                                         @PathVariable long requestId) {
        log.info("Get item request id {} by user id {}", requestId, userId);
        return requestClient.getRequest(userId, requestId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUserRequests(@RequestHeader(value = "X-Sharer-User-Id") long userId) {
        log.info("Get all user requests user id {}", userId);
        return requestClient.getUserRequests(userId);
    }


    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader(value = "X-Sharer-User-Id") long userId,
                                               @RequestParam(required = false) Integer from,
                                               @RequestParam(required = false) Integer size) {
        log.info("Get all requests by user id {} from {} size {}", userId, from, size);
        return requestClient.getAllRequests(userId, from, size);
    }
}