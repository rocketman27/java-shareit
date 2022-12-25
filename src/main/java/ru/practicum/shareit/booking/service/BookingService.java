package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {

    BookingDto approveOrRejectBooking(long bookingId, boolean approved, long userId);

    BookingDto getBookingById(long bookingId, long userId);

    List<BookingDto> getAllBookingsByBooker(String status, long userId);

    List<BookingDto> getAllBookingsByOwner(String state, long userId);

    BookingDto createBooking(long userId, BookingDto bookingDto);
}
