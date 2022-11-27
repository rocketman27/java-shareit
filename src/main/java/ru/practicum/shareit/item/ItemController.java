package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.validation.OnCreate;
import ru.practicum.shareit.validation.OnUpdate;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable long itemId) {
        return itemService.getItemById(itemId);
    }

    @GetMapping()
    public List<ItemDto> getAllItems(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.getAllItems(userId);
    }

    @PostMapping
    public ItemDto createItem(@Validated(OnCreate.class) @RequestBody ItemDto itemDto,
                              @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto patchUser(@Validated(OnUpdate.class) @PathVariable long itemId,
                             @RequestHeader("X-Sharer-User-Id") long userId,
                             @RequestBody Map<String, Object> fields) {
        return itemService.patchItem(itemId, userId, fields);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam String text, @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.searchItem(text, userId);
    }
}
