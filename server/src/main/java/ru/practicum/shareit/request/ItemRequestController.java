package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.RequestService;

import java.util.List;


@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {
    private final RequestService requestService;

    @PostMapping
    public ItemRequestDto addNewItemRequest(@RequestHeader(value = "X-Sharer-User-Id") long userId,
                                            @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Creating item request from user id {}", userId);
        return requestService.addNewRequest(userId, itemRequestDto);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getItemRequest(@RequestHeader(value = "X-Sharer-User-Id") long userId,
                                         @PathVariable long requestId) {
        log.info("Getting item request id {} from user id {}", requestId, userId);
        return requestService.getRequest(userId, requestId);
    }

    @GetMapping
    public List<ItemRequestDto> getAllUserRequests(@RequestHeader(value = "X-Sharer-User-Id") long userId) {
        log.info("Getting all user requests user id {}", userId);
        return requestService.getUserRequests(userId);
    }


    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(@RequestHeader(value = "X-Sharer-User-Id") long userId,
                                               @RequestParam(required = false) Integer from,
                                               @RequestParam(required = false) Integer size) {
        log.info("Getting all requests user id {} from {} size {}", userId, from, size);
        return requestService.getAllRequests(userId, from, size);
    }
}
