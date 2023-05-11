package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.exceptions.ItemExistsException;
import ru.practicum.shareit.item.exceptions.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Integer, Item> items;
    private int id = 0;

    @Override
    public Item createItem(Item item) {
        if (items.containsKey(item.getId())) {
            log.debug("Item id {} already exists", item.getId());
            throw new ItemExistsException(String.format("Item id %d already exists", item.getId()));
        }

        id++;
        item.setId(id);
        items.put(id, item);
        return item;
    }

    @Override
    public Item readItem(int itemId) {
        if (!items.containsKey(itemId)) {
            log.debug("Item id {} not found", itemId);
            throw new ItemNotFoundException(String.format("Item id - %d not found", itemId));
        }

        return items.get(itemId);
    }

    @Override
    public Item updateItem(Item item) {
        Item inMemoryItem = items.get(item.getId());

        if (item.getName() != null) {
            inMemoryItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            inMemoryItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            inMemoryItem.setAvailable(item.getAvailable());
        }

        return inMemoryItem;
    }

    @Override
    public List<Item> readUserItems(int userId) {
        return items.values().stream()
                .filter(item -> item.getOwnerId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> findItem(String text) {
        if (text.isEmpty())
            return Collections.emptyList();

        return items.values().stream()
                .filter(item -> (StringUtils.containsIgnoreCase(item.getName(), text) ||
                        StringUtils.containsIgnoreCase(item.getDescription(), text)) &&
                        item.getAvailable())
                .collect(Collectors.toList());
    }
}
