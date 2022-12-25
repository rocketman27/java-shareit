package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public class BookingMapper {

    public static BookingDto toBookingDto(Booking booking) {
        return BookingDto.builder()
                         .withId(booking.getId())
                         .withEnd(booking.getEnd())
                         .withStart(booking.getStart())
                         .withItem(booking.getItem())
                         .withBooker(booking.getBooker())
                         .withStatus(booking.getStatus())
                         .build();
    }

    public static Booking toBooking(User user, Item item, BookingDto bookingDto) {
        return Booking.builder()
                      .withEnd(bookingDto.getEnd())
                      .withStart(bookingDto.getStart())
                      .withItem(item)
                      .withBooker(user)
                      .build();
    }
}
