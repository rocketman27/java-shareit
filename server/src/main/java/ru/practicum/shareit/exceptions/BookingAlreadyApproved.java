package ru.practicum.shareit.exceptions;

public class BookingAlreadyApproved extends RuntimeException {

    public BookingAlreadyApproved(String message) {
        super(message);
    }
}
