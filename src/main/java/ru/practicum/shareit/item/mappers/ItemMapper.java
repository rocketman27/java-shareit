package ru.practicum.shareit.item.mappers;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                      .withId(item.getId())
                      .withName(item.getName())
                      .withDescription(item.getDescription())
                      .withAvailable(item.getAvailable())
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
