package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.item.exceptions.AccessRestrictedException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto addItem(ItemDto itemDto) {
        User user = userRepository.findById(itemDto.getUserId())
                .orElseThrow(() -> new UserNotFoundException(String.format("User id %d not found", itemDto.getUserId())));
        Item item = new Item(itemDto.getId(), user, itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable());

        return ItemDto.of(itemRepository.save(item));
    }

//    @Override
//    public Item updateItem(Item item) {
//        validateUserExist(item);
//        validateItemOwner(item);
//        validateItemAccess(item);
//
//        return itemRepository.updateItem(item);
//    }
//
//    @Override
//    public Item getItem(int itemId) {
//        return itemRepository.readItem(itemId);
//    }
//
//    @Override
//    public List<Item> getUserItems(int userId) {
//        userRepository.readUser(userId);
//
//        return itemRepository.readUserItems(userId);
//    }
//
//    @Override
//    public List<Item> findItem(String text) {
//        return itemRepository.findItem(text);
//    }
//
//    private void validateItemOwner(Item item) {
//        Item dbItem = getItem(item.getId());
//
//        if (!item.getOwnerId().equals(dbItem.getOwnerId())) {
//            log.debug("Item id {} owner mismatch", item.getId());
//            throw new UserNotFoundException(String.format("Item id - %d owner mismatch", item.getId()));
//        }
//    }
//
//    private void validateItemAccess(Item item) {
//        Item dbItem = getItem(item.getId());
//
//        if (!item.getOwnerId().equals(dbItem.getOwnerId())) {
//            log.debug("User id {} have not access to patch item id {}", item.getOwnerId(), item.getId());
//            throw new AccessRestrictedException(String.format("User id - %d have not access to patch item id - %d",
//                    item.getOwnerId(), item.getId()));
//        }
//    }
}
