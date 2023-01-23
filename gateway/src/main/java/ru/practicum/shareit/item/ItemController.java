package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.validation.OnCreate;
import ru.practicum.shareit.validation.OnUpdate;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Map;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId) {
        log.info("Getting an item by itemId={} for owner with userId={}", itemId, userId);
        return itemClient.getItemById(userId, itemId);
    }

    @GetMapping()
    public ResponseEntity<Object> getAllItems(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @PositiveOrZero @RequestParam(name = "from", required = false) Integer from,
                                              @Positive @RequestParam(name = "size", required = false) Integer size) {
        log.info("Getting all items for owner with userId={}, pageable from: {}, size: {}", userId, from, size);
        if (from != null && size != null) {
            return itemClient.getAllItems(userId, from, size);
        } else {
            return itemClient.getAllItems(userId);
        }
    }

    @PostMapping
    public ResponseEntity<Object> createItem(@Validated(OnCreate.class) @RequestBody ItemDto itemDto,
                                             @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Creating an item by item {} by owner with userId={}", itemDto, userId);
        return itemClient.createItem(userId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@Validated(OnCreate.class) @RequestBody CommentDto commentDto,
                                                @PathVariable long itemId, @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Creating a comment {} for item with itemId={} by owner with userId={}", commentDto, itemId, userId);
        return itemClient.createComment(userId, itemId, commentDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> patchItem(@Validated(OnUpdate.class) @PathVariable long itemId,
                                            @RequestHeader("X-Sharer-User-Id") long userId,
                                            @RequestBody Map<String, Object> body) {
        log.info("Patching an item {} with itemId={} by owner with userId={}", body, itemId, userId);
        return itemClient.patchItem(itemId, userId, body);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(@RequestParam String text,
                                             @RequestHeader("X-Sharer-User-Id") long userId,
                                             @PositiveOrZero @RequestParam(name = "from", required = false) Integer from,
                                             @Positive @RequestParam (name = "from", required = false) Integer size) {
        log.info("Searching items with text={} by owner with userId={}", text, userId);
        if (from != null && size != null) {
            return itemClient.searchItem(userId, text, from, size);
        } else {
            return itemClient.searchItem(userId, text);
        }
    }
}
