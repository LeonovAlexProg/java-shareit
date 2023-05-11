package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    Item createItem(Item item);

    Item readItem(int itemId);

    Item updateItem(Item item);

    List<Item> readUserItems(int userId);

    List<Item> findItem(String text);
}
