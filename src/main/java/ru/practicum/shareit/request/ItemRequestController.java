package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemResponseDto;
import ru.practicum.shareit.request.service.RequestService;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final RequestService requestService;

    @PostMapping
    public ItemRequestDto addNewItemRequest(@RequestHeader(value = "X-Sharer-User-Id") long userId,
                                            @Valid @RequestBody ItemRequestDto itemRequestDto) {
        return requestService.addNewRequest(userId, itemRequestDto);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getItemRequest(@PathVariable long requestId) {
        return requestService.getRequest(requestId);
    }

    @GetMapping
    public List<ItemRequestDto> getAllUserRequests(@RequestHeader(value = "X-Sharer-User-Id") long userId) {
        return requestService.getUserRequests(userId);
    }


    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(@RequestParam(required = false) Integer from,
                                               @RequestParam(required = false) Integer size) {
        return requestService.getAllRequests(from, size);
    }
}
