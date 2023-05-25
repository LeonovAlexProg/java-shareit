package ru.practicum.shareit.item.exceptions;

public class ItemExistsException extends RuntimeException {
    public ItemExistsException(String message) {
        super(message);
    }
}
