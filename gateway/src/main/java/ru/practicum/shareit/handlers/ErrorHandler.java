package ru.practicum.shareit.handlers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.exceptions.UnsupportedStateException;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.request.RequestController;
import ru.practicum.shareit.user.UserController;

import java.util.Map;

@Slf4j
@RestControllerAdvice(assignableTypes = {
        BookingController.class,
        ItemController.class,
        UserController.class,
        RequestController.class
})
public class ErrorHandler {

    @ExceptionHandler(value = {UnsupportedStateException.class})
    public ResponseEntity<Map<String, String>> handleUnsupportedStateException(final UnsupportedStateException e) {
        log.error("Server returned HttpCode 400: {}", e.getMessage(), e);
        return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {Throwable.class})
    public ResponseEntity<Map<String, String>> handleThrowable(final Throwable e) {
        log.error("Server returned HttpCode 400: {}", e.getMessage(), e);
        return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.BAD_REQUEST);
    }
}
