package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;
import java.util.Map;

public interface ItemService {
    ItemDto getItemById(long itemId);

    List<ItemDto> getAllItems(long userId);

    ItemDto createItem(long userId, ItemDto itemDto);

    ItemDto patchItem(long itemId, long userId, Map<String, Object> fields);

    List<ItemDto> searchItem(String text, long userId);
}
