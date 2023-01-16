package ru.practicum.shareit.request.mappers;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

public class ItemRequestMapper {

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                             .withId(itemRequest.getId())
                             .withDescription(itemRequest.getDescription())
                             .withCreated(itemRequest.getCreated())
                             .build();
    }

    public static ItemRequestDto.Item toItemRequestDtoItem(Item item) {
        return ItemRequestDto.Item.builder()
                                  .withId(item.getId())
                                  .withName(item.getName())
                                  .withDescription(item.getDescription())
                                  .withIsAvailable(item.getAvailable())
                                  .withRequestId(item.getRequest().getId())
                                  .build();
    }

    public static ItemRequest toItemRequest(User requester, ItemRequestDto itemRequestDto) {
        return ItemRequest.builder()
                          .withId(itemRequestDto.getId())
                          .withDescription(itemRequestDto.getDescription())
                          .withRequestor(requester)
                          .withCreated(itemRequestDto.getCreated())
                          .build();
    }
}
