package ru.practicum.shareit.exceptions;

public class InvalidPageableParametersException extends RuntimeException {

    public InvalidPageableParametersException(String message) {
        super(message);
    }
}
