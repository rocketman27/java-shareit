package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exceptions.UnsupportedStateException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveOrRejectBooking(@PathVariable long bookingId,
                                                         @RequestParam boolean approved,
                                                         @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingClient.approveOrRejectBooking(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @PathVariable long bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.getBookingById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllBookingsByBooker(@RequestHeader("X-Sharer-User-Id") long userId,
                                                         @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                                         @PositiveOrZero @RequestParam(name = "from", required = false) Integer from,
                                                         @Positive @RequestParam(name = "size", required = false) Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new UnsupportedStateException("Unknown state: " + stateParam));
        log.info("Get booking by a booker with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        if (from != null && size != null) {
            return bookingClient.getBookingsByBooker(userId, state, from, size);
        } else {
            return bookingClient.getBookingsByBooker(userId, state);
        }
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllBookingsByOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                                        @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                                        @PositiveOrZero @RequestParam(name = "from", required = false) Integer from,
                                                        @Positive @RequestParam(name = "size", required = false) Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new UnsupportedStateException("Unknown state: " + stateParam));
        log.info("Get booking by an owner with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        if (from != null && size != null) {
            return bookingClient.getBookingsByOwner(userId, state, from, size);
        } else {
            return bookingClient.getBookingsByOwner(userId, state);
        }
    }

    @PostMapping
    public ResponseEntity<Object> bookItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @RequestBody @Valid BookingDto requestDto) {
        log.info("Creating booking {}, userId={}", requestDto, userId);
        return bookingClient.bookItem(userId, requestDto);
    }
}
