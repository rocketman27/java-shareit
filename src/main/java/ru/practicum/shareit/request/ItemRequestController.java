package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.validation.OnCreate;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @Autowired
    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @GetMapping("/{itemRequestId}")
    public ItemRequestDto getItemRequestById(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable long itemRequestId) {
        return itemRequestService.getItemRequestById(userId, itemRequestId);
    }

    @GetMapping()
    public List<ItemRequestDto> getItemRequestsByUserId(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestService.getItemRequestsByUserId(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllItemRequests(@RequestHeader("X-Sharer-User-Id") long userId,
                                                   @RequestParam(required = false) Integer from,
                                                   @RequestParam(required = false) Integer size) {
        if (from != null && size != null) {
            return itemRequestService.getAllItemRequests(userId, from, size);
        } else {
            return itemRequestService.getAllItemRequests(userId);
        }
    }

    @PostMapping
    public ItemRequestDto createItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                     @Validated(OnCreate.class) @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestService.createItemRequest(userId, itemRequestDto);
    }
}
