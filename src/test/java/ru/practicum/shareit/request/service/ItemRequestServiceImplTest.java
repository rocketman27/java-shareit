package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.ItemRequestNotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repositories.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repositories.ItemRequestRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {
    @Mock
    private UserRepository mockUserRepository;
    @Mock
    private ItemRepository mockItemRepository;
    @Mock
    private ItemRequestRepository mockItemRequestRepository;
    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;


    @Test
    void createItemRequestShouldThrowExceptionWhenUserDoesNotExistTest() {
        Mockito.when(mockUserRepository.findById(Mockito.anyLong()))
               .thenThrow(new UserNotFoundException(format("User with userId=%s is not found", 1)));

        ItemRequestDto itemRequestDto = getTestItemRequestDto();

        Exception exception = Assertions.assertThrows(UserNotFoundException.class,
                () -> itemRequestService.createItemRequest(1, itemRequestDto));

        Assertions.assertEquals(exception.getMessage(), "User with userId=1 is not found");
    }

    @Test
    void shouldCreateItemRequestTest() {
        LocalDateTime time = LocalDateTime.now();
        User requester = getTestUser(1);

        Mockito.when(mockUserRepository.findById(Mockito.anyLong()))
               .thenReturn(Optional.of(requester));

        ItemRequestDto requestDto = getTestItemRequestDto();

        ItemRequest itemRequest = getTestItemRequestWithoutItems(1, time, requester);

        Mockito.when(mockItemRequestRepository.save(Mockito.any(ItemRequest.class)))
               .thenReturn(itemRequest);

        ItemRequestDto expectedRequestDto = ItemRequestDto.builder()
                                                          .withId(1)
                                                          .withDescription("Test")
                                                          .withCreated(time)
                                                          .build();

        ItemRequestDto actualRequestDto = itemRequestService.createItemRequest(1, requestDto);

        Assertions.assertEquals(actualRequestDto, expectedRequestDto);
    }

    @Test
    void shouldReturnItemRequestWithoutItemsByIdTest() {
        LocalDateTime time = LocalDateTime.now();
        User requester = getTestUser(1);

        Mockito.when(mockUserRepository.findById(Mockito.anyLong()))
               .thenReturn(Optional.of(requester));

        ItemRequest itemRequest = getTestItemRequestWithoutItems(1, time, requester);

        Mockito.when(mockItemRequestRepository.findById(Mockito.anyLong()))
               .thenReturn(Optional.of(itemRequest));

        ItemRequestDto expectedItemRequestDto = ItemRequestDto.builder()
                                                              .withId(1)
                                                              .withDescription("Test")
                                                              .withCreated(time)
                                                              .withItems(Collections.emptyList())
                                                              .build();

        ItemRequestDto actualItemRequestDto = itemRequestService.getItemRequestById(1, 1);

        Assertions.assertEquals(expectedItemRequestDto, actualItemRequestDto);
    }

    @Test
    void shouldReturnItemRequestWithItemsByIdTest() {
        long itemId = 1;
        long requesterId = 1;
        long ownerId = 2;
        long itemRequestId = 1;

        LocalDateTime time = LocalDateTime.now();
        User requester = getTestUser(requesterId);

        Mockito.when(mockUserRepository.findById(Mockito.anyLong()))
               .thenReturn(Optional.of(requester));

        ItemRequest itemRequest = getTestItemRequestWithoutItems(itemRequestId, time, requester);

        Mockito.when(mockItemRequestRepository.findById(Mockito.anyLong()))
               .thenReturn(Optional.of(itemRequest));

        List<Item> items = List.of(Item.builder()
                                       .withId(itemId)
                                       .withDescription("Item's description")
                                       .withName("Item")
                                       .withAvailable(true)
                                       .withRequest(itemRequest)
                                       .withOwner(User.builder()
                                                      .withId(ownerId)
                                                      .withName("Owner")
                                                      .withEmail("owner@gmail.com")
                                                      .build())
                                       .build());

        Mockito.when(mockItemRepository.findByRequestId(Mockito.anyLong()))
               .thenReturn(items);

        ItemRequestDto expectedItemRequestDto = ItemRequestDto.builder()
                                                              .withId(itemRequestId)
                                                              .withDescription("Test")
                                                              .withCreated(time)
                                                              .withItems(List.of(ItemRequestDto.Item.builder()
                                                                                                    .withId(itemId)
                                                                                                    .withDescription("Item's description")
                                                                                                    .withName("Item")
                                                                                                    .withRequestId(itemRequestId)
                                                                                                    .withIsAvailable(true)
                                                                                                    .build()))
                                                              .build();

        ItemRequestDto actualItemRequestDto = itemRequestService.getItemRequestById(1, 1);

        Assertions.assertEquals(expectedItemRequestDto, actualItemRequestDto);
    }

    @Test
    void getItemRequestByIdShouldThrowExceptionWhenUserDoesNotExistTest() {
        long userId = 1;
        long itemRequestId = 1;

        Mockito.when(mockUserRepository.findById(Mockito.anyLong()))
               .thenThrow(new UserNotFoundException(format("User with userId=%s is not found", userId)));

        Exception exception = Assertions.assertThrows(UserNotFoundException.class,
                () -> itemRequestService.getItemRequestById(userId, itemRequestId));

        Assertions.assertEquals(exception.getMessage(), String.format("User with userId=%s is not found", userId));
    }

    @Test
    void getItemRequestByIdShouldThrowExceptionWhenItemRequestDoesNotExistTest() {
        long userId = 1;
        long itemRequestId = 1;

        User user = getTestUser(userId);

        Mockito.when(mockUserRepository.findById(Mockito.anyLong()))
               .thenReturn(Optional.of(user));

        Mockito.when(mockItemRequestRepository.findById(Mockito.anyLong()))
               .thenThrow(new ItemRequestNotFoundException(format("ItemRequest with itemRequestId=%s is not found", itemRequestId)));

        Exception exception = Assertions.assertThrows(ItemRequestNotFoundException.class,
                () -> itemRequestService.getItemRequestById(userId, itemRequestId));

        Assertions.assertEquals(exception.getMessage(), String.format("ItemRequest with itemRequestId=%s is not found", itemRequestId));
    }

    private User getTestUser(long id) {
        return User.builder()
                   .withId(id)
                   .withName("Test")
                   .withEmail("test@gmail.com")
                   .build();
    }

    private ItemRequest getTestItemRequestWithoutItems(long id, LocalDateTime time, User requester) {
        return ItemRequest.builder()
                          .withId(id)
                          .withDescription("Test")
                          .withRequestor(requester)
                          .withCreated(time)
                          .build();
    }

    private ItemRequestDto getTestItemRequestDto() {
        return ItemRequestDto.builder()
                             .withDescription("Test")
                             .build();
    }
}
