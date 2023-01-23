package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;
import java.util.Map;

public interface ItemService {
    ItemDto getItemById(long userId, long itemId);

    List<ItemDto> getAllItems(long userId);

    List<ItemDto> getAllItems(long userId, int from, int size);

    ItemDto createItem(long userId, ItemDto itemDto);

    CommentDto createComment(long userId, long itemId, CommentDto commentDto);

    ItemDto patchItem(long itemId, long userId, Map<String, Object> fields);

    List<ItemDto> searchItem(String text, long userId);

    List<ItemDto> searchItem(String text, long userId, int from, int size);
}
