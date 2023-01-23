package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.validation.OnCreate;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class RequestController {
    private final RequestClient requestClient;

    @GetMapping("/{itemRequestId}")
    public ResponseEntity<Object> getItemRequestById(@RequestHeader("X-Sharer-User-Id") long userId,
                                                     @PathVariable long itemRequestId) {
        log.info("Getting an item request by itemRequestId={} and a requester with userId={}", itemRequestId, userId);
        return requestClient.getItemRequestById(itemRequestId, userId);
    }

    @GetMapping()
    public ResponseEntity<Object> getItemRequestsByUserId(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Getting item requests by a requester with userId={}", userId);
        return requestClient.getItemRequestsByUserId(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequests(@RequestHeader("X-Sharer-User-Id") long userId,
                                                   @PositiveOrZero @RequestParam(required = false) Integer from,
                                                   @Positive @RequestParam(required = false) Integer size) {
        log.info("Getting all item requests");
        if (from != null && size != null) {
            return requestClient.getAllItemRequests(userId, from, size);
        } else {
            return requestClient.getAllItemRequests(userId);
        }
    }

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                     @Validated(OnCreate.class) @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Creating an item request {}, requester has userId={}", itemRequestDto, userId);
        return requestClient.createItemRequest(userId, itemRequestDto);
    }
}
