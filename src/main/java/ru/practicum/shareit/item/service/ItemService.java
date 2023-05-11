package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item addItem(Item item);

    Item updateItem(Item item);

    Item getItem(int itemId);

    List<Item> getUserItems(int userId);

    List<Item> findItem(String text);
}
