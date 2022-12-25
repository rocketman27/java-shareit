package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveOrRejectBooking(@PathVariable long bookingId,
                                             @RequestParam boolean approved,
                                             @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingService.approveOrRejectBooking(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getItemById(@PathVariable long bookingId, @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingService.getBookingById(bookingId, userId);
    }

    @GetMapping()
    public List<BookingDto> getAllItemsByBooker(@RequestParam(defaultValue = "ALL") String state,
                                                @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingService.getAllBookingsByBooker(state, userId);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllItemsByOwner(@RequestParam(defaultValue = "ALL") String state,
                                                @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingService.getAllBookingsByOwner(state, userId);
    }


    @PostMapping
    public BookingDto createItem(@Validated @RequestBody BookingDto bookingDto,
                                 @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingService.createBooking(userId, bookingDto);
    }
}
