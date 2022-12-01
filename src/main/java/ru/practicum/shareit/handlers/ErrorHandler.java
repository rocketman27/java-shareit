package ru.practicum.shareit.handlers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exceptions.EmailAlreadyExistsException;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.exceptions.UserMismatchException;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.user.UserController;

import java.util.Map;

@Slf4j
@RestControllerAdvice(assignableTypes = {ItemController.class, UserController.class})
public class ErrorHandler {

    @ExceptionHandler(value = {EntityNotFoundException.class})
    public ResponseEntity<Map<String, String>> handleNotFoundException(final EntityNotFoundException e) {
        log.error("Server returned HttpCode 404: {}", e.getMessage(), e);
        return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {UserMismatchException.class})
    public ResponseEntity<Map<String, String>> handleUserMismatchException(final UserMismatchException e) {
        log.error("Server returned HttpCode 403: {}", e.getMessage(), e);
        return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = {EmailAlreadyExistsException.class})
    public ResponseEntity<Map<String, String>> handleEmailAlreadyExistsException(final EmailAlreadyExistsException e) {
        log.error("Server returned HttpCode 409: {}", e.getMessage(), e);
        return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = {Throwable.class})
    public ResponseEntity<Map<String, String>> handleThrowable(final Throwable e) {
        log.error("Server returned HttpCode 400: {}", e.getMessage(), e);
        return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.BAD_REQUEST);
    }
}
