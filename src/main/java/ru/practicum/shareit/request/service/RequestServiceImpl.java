package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.request.exception.PaginationDataException;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;

    @Override
    public ItemRequestDto addNewRequest(long userId, ItemRequestDto itemRequestDto) {
        User creator = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("User id %d not found", userId)));

        ItemRequest newRequest = ItemRequest.builder()
                .created(LocalDateTime.now())
                .description(itemRequestDto.getDescription())
                .user(creator)
                .items(new ArrayList<>())
                .build();

        newRequest = requestRepository.save(newRequest);

        return ItemRequestMapper.mapToDto(newRequest);
    }

    @Override
    public ItemRequestDto getRequest(Long userId, Long requestId) {
        if (!userRepository.existsById(userId))
            throw new UserNotFoundException(String.format("User id %d not found", userId));

        ItemRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ItemRequestNotFoundException(String.format("Item request id %d not found", requestId)));

        return ItemRequestMapper.mapToDto(request);
    }

    @Override
    public List<ItemRequestDto> getUserRequests(long userId) {
        if (!userRepository.existsById(userId))
            throw new UserNotFoundException(String.format("User id %d not found", userId));

        List<ItemRequest> userRequests = requestRepository.findAllByUserId(userId);

        return ItemRequestMapper.mapListToDto(userRequests);
    }

    @Override
    public List<ItemRequestDto> getAllRequests(Long userId, Integer from, Integer size) {
        Pageable pageable;
        List<ItemRequest> requests;

        if (from != null && size != null) {
            if (from < 0 || size < 0) {
                throw new PaginationDataException("Invalid pagination data");
            }

            int page = from / size;
            pageable = PageRequest.of(page, size, Sort.by("created").descending());
            requests = requestRepository.findRequests(pageable);

            requests = requests.stream()
                    .filter(itemRequest -> !itemRequest.getUser().getId().equals(userId))
                    .collect(Collectors.toList());
        } else {
            requests = requestRepository.findRequests(Sort.by("created").descending());
        }

        return ItemRequestMapper.mapListToDto(requests);
    }
}
