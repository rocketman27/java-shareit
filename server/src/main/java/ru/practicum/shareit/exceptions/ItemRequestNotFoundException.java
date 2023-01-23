package ru.practicum.shareit.exceptions;

public class ItemRequestNotFoundException extends EntityNotFoundException {

    public ItemRequestNotFoundException(String message) {
        super(message);
    }
}
