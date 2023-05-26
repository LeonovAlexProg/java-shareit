package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exceptions.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.item.exceptions.ItemAccessRestrictedException;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @Override
    public ItemDto updateItem(ItemDto itemDto) {
        Item srcItem = Item.of(itemDto);
        Item trgItem = itemRepository.findById(itemDto.getId())
                .orElseThrow(() -> new ItemNotFoundException(String.format("Item id %d not found", itemDto.getId())));

        if (!itemDto.getUserId().equals(trgItem.getUser().getId())) {
            throw new ItemAccessRestrictedException(
                    String.format("User id %d have not access to patch Item id %d", srcItem.getUser().getId(), srcItem.getId())
            );
        }

        copyNonNullProperties(srcItem, trgItem);
        return ItemDto.of(itemRepository.save(trgItem));
    }

    @Override
    public ItemDto getItem(Long itemId) {
        return ItemDto.of(itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(String.format("Item id %d not found", itemId))));
    }

    @Override
    public List<ItemDto> getUserItems(Long userId) {
        if (userRepository.existsById(userId)) {
            return ItemDto.listOf(itemRepository.findItemsByUserId(userId)
                    .orElseThrow(() -> new ItemNotFoundException(String.format("User id %d have no any items", userId))));
        } else {
            throw new UserNotFoundException(String.format("User id %d not found", userId));
        }
    }

    @Override
    public List<ItemDto> findItem(String text) {
        if (text.isEmpty())
            return Collections.emptyList();

        return ItemDto.listOf(itemRepository.findItemsLike(text)
                .orElseThrow(() -> new ItemNotFoundException(String.format("No items containing %s were found", text))));
    }

    private static void copyNonNullProperties(Object src, Object target) {
       BeanUtils.copyProperties(src, target, getNullPropertyNames(src));
    }

    private static String[] getNullPropertyNames (Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<String>();
        for(java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) emptyNames.add(pd.getName());
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }
}
