package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.UserMismatchException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.String.*;

@Service
public class ItemServiceImpl implements ItemService {
    private static long nextId = 1;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ItemDto getItemById(long itemId) {
        Optional<Item> item = itemRepository.getItemById(itemId);
        if (item.isPresent()) {
            return ItemMapper.toItemDto(item.get());
        } else {
            throw new ItemNotFoundException(format("Item with userId=%s is not found", itemId));
        }
    }

    @Override
    public List<ItemDto> getAllItems(long userId) {
        return itemRepository.getAllItems()
                             .stream()
                             .filter(item -> item.getOwner().getId() == userId)
                             .map(ItemMapper::toItemDto)
                             .collect(Collectors.toList());
    }

    @Override
    public ItemDto createItem(long userId, ItemDto itemDto) {
        Optional<User> owner = userRepository.getUserById(userId);
        if (owner.isPresent()) {
            itemDto.setId(nextId++);
            Item item = ItemMapper.toItem(owner.get(), itemDto);
            return ItemMapper.toItemDto(itemRepository.createItem(item));
        } else {
            throw new UserNotFoundException(format("Owner with userId=%s is not found", userId));
        }
    }

    @Override
    public ItemDto patchItem(long itemId, long userId, Map<String, Object> fields) {
        Optional<Item> item = itemRepository.getItemById(itemId);
        Optional<User> owner = userRepository.getUserById(userId);

        if (item.isPresent()) {
            if (owner.isPresent()) {
                if (item.get().getOwner().getId() != owner.get().getId()) {
                    throw new UserMismatchException(format(
                            "X-Sharer-User-Id has incorrect value %s, item %s belongs to another user", userId, itemId)
                    );
                }
            } else {
                throw new UserNotFoundException(format("Owner with userId=%s is not found", userId));
            }
            fields.forEach((k, v) -> {
                Field field = ReflectionUtils.findField(Item.class, k);
                if (field != null) {
                    field.setAccessible(true);
                    ReflectionUtils.setField(field, item.get(), v);
                }
            });
            itemRepository.updateItem(item.get());
            return ItemMapper.toItemDto(item.get());
        } else {
            throw new ItemNotFoundException(format("Item with itemId=%s is not found", itemId));
        }
    }

    @Override
    public List<ItemDto> searchItem(String text, long userId) {
        if (text.isBlank()) {
            return Collections.emptyList();
        } else {
            return itemRepository.getAllItems()
                                 .stream()
                                 .filter(Item::getAvailable)
                                 .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase()) ||
                                         item.getDescription().toLowerCase().contains(text.toLowerCase()))
                                 .map(ItemMapper::toItemDto)
                                 .collect(Collectors.toList());
        }
    }
}
