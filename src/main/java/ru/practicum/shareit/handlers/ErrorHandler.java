package ru.practicum.shareit.handlers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.user.UserController;

import java.util.Map;

@Slf4j
@RestControllerAdvice(assignableTypes = {ItemController.class, UserController.class, BookingController.class})
public class ErrorHandler {

    @ExceptionHandler(value = {EntityNotFoundException.class})
    public ResponseEntity<Map<String, String>> handleNotFoundException(final EntityNotFoundException e) {
        log.error("Server returned HttpCode 404: {}", e.getMessage(), e);
        return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {UserMismatchException.class})
    public ResponseEntity<Map<String, String>> handleUserMismatchException(final UserMismatchException e) {
        log.error("Server returned HttpCode 404: {}", e.getMessage(), e);
        return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.NOT_FOUND);
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

    @ExceptionHandler(value = {DataIntegrityViolationException.class})
    public ResponseEntity<Map<String, String>> handleDataIntegrityViolationException(final DataIntegrityViolationException e) {
        log.error("Server returned HttpCode 409: {}", e.getMessage(), e);
        return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = {ItemUnavailableException.class})
    public ResponseEntity<Map<String, String>> handleItemUnavailableException(final ItemUnavailableException e) {
        log.error("Server returned HttpCode 400: {}", e.getMessage(), e);
        return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {UnsupportedStateException.class})
    public ResponseEntity<Map<String, String>> handleUnsupportedStateException(final UnsupportedStateException e) {
        log.error("Server returned HttpCode 400: {}", e.getMessage(), e);
        return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {BookingAlreadyApproved.class})
    public ResponseEntity<Map<String, String>> handleBookingAlreadyApproved(final BookingAlreadyApproved e) {
        log.error("Server returned HttpCode 400: {}", e.getMessage(), e);
        return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {ActionNotAllowedException.class})
    public ResponseEntity<Map<String, String>> handleActionNotAllowedException(final ActionNotAllowedException e) {
        log.error("Server returned HttpCode 404: {}", e.getMessage(), e);
        return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {CommentNotAllowedException.class})
    public ResponseEntity<Map<String, String>> handleCommentNotAllowedException(final CommentNotAllowedException e) {
        log.error("Server returned HttpCode 400: {}", e.getMessage(), e);
        return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.BAD_REQUEST);
    }
}
