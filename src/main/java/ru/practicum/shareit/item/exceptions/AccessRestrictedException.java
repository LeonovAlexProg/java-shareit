package ru.practicum.shareit.item.exceptions;

public class AccessRestrictedException extends RuntimeException {
    public AccessRestrictedException(String message) {
        super(message);
    }
}
