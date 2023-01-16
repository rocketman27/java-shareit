package ru.practicum.shareit.item.mappers;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                      .withId(item.getId())
                      .withName(item.getName())
                      .withDescription(item.getDescription())
                      .withAvailable(item.getAvailable())
                      .withRequestId(item.getRequest() != null ? item.getRequest().getId() : 0)
                      .build();
    }

    public static ItemDto.Booking toItemDtoBooking(Booking booking) {
        return ItemDto.Booking.builder()
                              .withId(booking.getId())
                              .withBookerId(booking.getBooker().getId())
                              .withStart(booking.getStart())
                              .withEnd(booking.getEnd())
                              .build();
    }

    public static ItemDto.Comment toItemDtoComment(Comment comment) {
        return ItemDto.Comment.builder().withId(comment.getId())
                              .withText(comment.getText())
                              .withAuthorName(comment.getAuthor().getName())
                              .withCreated(comment.getCreated())
                              .build();
    }

    public static Item toItem(User owner, ItemDto itemDto) {
        return Item.builder()
                   .withId(itemDto.getId())
                   .withOwner(owner)
                   .withName(itemDto.getName())
                   .withDescription(itemDto.getDescription())
                   .withAvailable(itemDto.getAvailable())
                   .build();
    }
}
