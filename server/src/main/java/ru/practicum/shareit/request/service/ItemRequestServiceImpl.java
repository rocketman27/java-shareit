package ru.practicum.shareit.request.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.InvalidPageableParametersException;
import ru.practicum.shareit.exceptions.ItemRequestNotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.repositories.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mappers.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repositories.ItemRequestRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.utils.PageableUtils;

import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Service
public class ItemRequestServiceImpl implements ItemRequestService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Autowired
    public ItemRequestServiceImpl(UserRepository userRepository,
                                  ItemRepository itemRepository,
                                  ItemRequestRepository itemRequestRepository) {
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.itemRequestRepository = itemRequestRepository;
    }

    @Transactional
    @Override
    public ItemRequestDto createItemRequest(long userId, ItemRequestDto itemRequestDto) {
        User requester = userRepository.findById(userId)
                                       .orElseThrow(() -> new UserNotFoundException(
                                               format("User with userId=%s is not found", userId))
                                       );

        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(requester, itemRequestDto);

        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public ItemRequestDto getItemRequestById(long userId, long itemRequestId) {
        userRepository.findById(userId)
                      .orElseThrow(() -> new UserNotFoundException(
                              format("Requester with userId=%s is not found", userId))
                      );

        ItemRequest itemRequest = itemRequestRepository.findById(itemRequestId)
                                                       .orElseThrow(() -> new ItemRequestNotFoundException(
                                                               format("ItemRequest with itemRequestId=%s is not found", itemRequestId))
                                                       );

        return findAndSetItemRequestItems(itemRequest);
    }

    @Override
    public List<ItemRequestDto> getItemRequestsByUserId(long requestorId) {
        userRepository.findById(requestorId)
                      .orElseThrow(() -> new UserNotFoundException(
                              format("Requester with userId=%s is not found", requestorId))
                      );

        return itemRequestRepository.findByRequestorIdOrderByCreated(requestorId)
                                    .stream()
                                    .map(this::findAndSetItemRequestItems)
                                    .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAllItemRequests(long userId) {
        return itemRequestRepository.findByRequestorIdNot(userId)
                                    .stream()
                                    .map(this::findAndSetItemRequestItems)
                                    .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAllItemRequests(long userId, int from, int size) {
        if (PageableUtils.isInvalidFromAndSize(from, size)) {
            throw new InvalidPageableParametersException("Invalid pageable parameters");
        }
        return itemRequestRepository.findByRequestorIdNot(userId, PageRequest.of(from / size, size))
                                    .stream()
                                    .map(this::findAndSetItemRequestItems)
                                    .collect(Collectors.toList());
    }

    private ItemRequestDto findAndSetItemRequestItems(ItemRequest itemRequest) {
        List<ItemRequestDto.Item> items = itemRepository.findByRequestId(itemRequest.getId())
                                                        .stream()
                                                        .map(ItemRequestMapper::toItemRequestDtoItem)
                                                        .collect(Collectors.toList());

        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);

        itemRequestDto.setItems(items);

        return itemRequestDto;
    }
}
