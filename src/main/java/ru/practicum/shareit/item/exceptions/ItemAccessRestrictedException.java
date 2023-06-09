package ru.practicum.shareit.item.exceptions;

public class ItemAccessRestrictedException extends RuntimeException {
    public ItemAccessRestrictedException(String message) {
        super(message);
    }
}
