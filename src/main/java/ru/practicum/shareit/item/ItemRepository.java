package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class ItemRepository {
    private static long nextId = 1;
    private final Map<Long, Item> items = new HashMap<>();

    public Optional<Item> getItemById(long userId) {
        return java.util.Optional.ofNullable(items.get(userId));
    }

    public List<Item> getAllItems() {
        return new ArrayList<>(items.values());
    }

    public ItemDto createItem(User owner, ItemDto itemDto) {
        itemDto.setId(nextId++);
        items.put(itemDto.getId(), ItemMapper.toItem(owner, itemDto));
        return itemDto;
    }

    public void updateItem(Item item) {
        items.put(item.getId(), item);
    }

    public void deleteItem(long userId) {
        items.remove(userId);
    }
}
