package ru.practicum.shareit.exceptions;

public class UserMismatchException extends RuntimeException {

    public UserMismatchException(String message) {
        super(message);
    }
}
