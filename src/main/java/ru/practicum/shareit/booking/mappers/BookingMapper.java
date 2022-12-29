package ru.practicum.shareit.booking.mappers;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public class BookingMapper {

    public static BookingDto toBookingDto(Booking booking) {
        BookingDto.Item item = BookingDto.Item.builder()
                                              .withId(booking.getItem().getId())
                                              .withName(booking.getItem().getName())
                                              .build();

        BookingDto.Booker booker = BookingDto.Booker.builder()
                                                    .withId(booking.getBooker().getId())
                                                    .build();

        return BookingDto.builder()
                         .withId(booking.getId())
                         .withEnd(booking.getEnd())
                         .withStart(booking.getStart())
                         .withItem(item)
                         .withBooker(booker)
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
