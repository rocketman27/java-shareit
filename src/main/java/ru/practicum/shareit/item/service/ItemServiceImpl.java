package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repositories.BookingRepository;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mappers.CommentMapper;
import ru.practicum.shareit.item.mappers.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repositories.CommentRepository;
import ru.practicum.shareit.item.repositories.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repositories.ItemRequestRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.utils.PageableUtils;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static ru.practicum.shareit.booking.model.BookingStatus.APPROVED;
import static ru.practicum.shareit.item.mappers.ItemMapper.toItemDto;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository,
                           UserRepository userRepository,
                           BookingRepository bookingRepository,
                           CommentRepository commentRepository,
                           ItemRequestRepository itemRequestRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
        this.itemRequestRepository = itemRequestRepository;
    }

    @Override
    public ItemDto getItemById(long userId, long itemId) {
        Item item = itemRepository.findById(itemId)
                                  .orElseThrow(() -> new ItemNotFoundException(format("Item with itemId=%s is not found", itemId)));

        ItemDto itemDto = toItemDto(item);

        List<Booking> bookings = bookingRepository.findByItemIdAndOwnerIdOrderByStartDesc(itemId, userId);
        LocalDateTime currentTime = LocalDateTime.now();

        setLastAndNextBookings(currentTime, itemDto, bookings);

        List<ItemDto.Comment> comments = commentRepository.findByItemId(itemId)
                                                          .stream()
                                                          .map(ItemMapper::toItemDtoComment)
                                                          .collect(Collectors.toList());

        itemDto.setComments(comments);

        return itemDto;
    }

    @Override
    public List<ItemDto> getAllItems(long userId) {
        LocalDateTime currentTime = LocalDateTime.now();
        return itemRepository.findAll()
                             .stream()
                             .filter(item -> item.getOwner().getId() == userId)
                             .map(item -> {
                                 ItemDto itemDto = ItemMapper.toItemDto(item);
                                 List<ItemDto.Comment> comments = commentRepository.findByAuthorIdAndItemId(userId, item.getId())
                                                                                   .stream()
                                                                                   .map(ItemMapper::toItemDtoComment)
                                                                                   .collect(Collectors.toList());
                                 itemDto.setComments(comments);

                                 List<Booking> bookings = bookingRepository.findByItemIdAndOwnerIdOrderByStartDesc(item.getId(), userId);
                                 setLastAndNextBookings(currentTime, itemDto, bookings);

                                 return itemDto;
                             })
                             .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getAllItems(long userId, int from, int size) {
        if (PageableUtils.isInvalidFromAndSize(from, size)) {
            throw new InvalidPageableParametersException("Invalid pageable parameters");
        }
        LocalDateTime currentTime = LocalDateTime.now();
        return itemRepository.findAll(PageRequest.of(from, size))
                             .stream()
                             .filter(item -> item.getOwner().getId() == userId)
                             .map(item -> {
                                 ItemDto itemDto = ItemMapper.toItemDto(item);
                                 List<ItemDto.Comment> comments = commentRepository.findByAuthorIdAndItemId(userId, item.getId())
                                                                                   .stream()
                                                                                   .map(ItemMapper::toItemDtoComment)
                                                                                   .collect(Collectors.toList());
                                 itemDto.setComments(comments);

                                 List<Booking> bookings = bookingRepository.findByItemIdAndOwnerIdOrderByStartDesc(item.getId(), userId);
                                 setLastAndNextBookings(currentTime, itemDto, bookings);

                                 return itemDto;
                             })
                             .collect(Collectors.toList());
    }

    private void setLastAndNextBookings(LocalDateTime currentTime, ItemDto itemDto, List<Booking> bookings) {
        Optional<Booking> lastBooking = getLastBooking(currentTime, bookings);
        lastBooking.ifPresent(b -> itemDto.setLastBooking(ItemMapper.toItemDtoBooking(b)));

        Optional<Booking> nextBooking = getNextBooking(currentTime, bookings);
        nextBooking.ifPresent(b -> itemDto.setNextBooking(ItemMapper.toItemDtoBooking(b)));
    }

    private static Optional<Booking> getLastBooking(LocalDateTime currentTime, List<Booking> bookings) {
        return bookings.stream()
                       .filter(booking -> booking.getEnd().isBefore(currentTime))
                       .limit(1)
                       .findFirst();
    }

    private static Optional<Booking> getNextBooking(LocalDateTime currentTime, List<Booking> bookings) {
        return bookings.stream()
                       .filter(booking -> booking.getEnd().isAfter(currentTime))
                       .limit(1)
                       .findFirst();
    }

    @Override
    public ItemDto createItem(long userId, ItemDto itemDto) {
        User owner = userRepository.findById(userId)
                                   .orElseThrow(() -> new UserNotFoundException(format("Owner with userId=%s is not found", userId)));

        Item item = ItemMapper.toItem(owner, itemDto);

        long itemRequestId = itemDto.getRequestId();

        if (itemRequestId != 0) {
            ItemRequest itemRequest = itemRequestRepository.findById(itemDto.getRequestId())
                                                           .orElseThrow(() -> new ItemRequestNotFoundException(
                                                                   format("ItemRequest with itemRequestId=%s is not found", itemRequestId))
                                                           );
            item.setRequest(itemRequest);
        }

        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public CommentDto createComment(long userId, long itemId, CommentDto commentDto) {
        User author = userRepository.findById(userId)
                                    .orElseThrow(() -> new UserNotFoundException(format("Owner with userId=%s is not found", userId)));

        Item item = itemRepository.findById(itemId)
                                  .orElseThrow(() -> new ItemNotFoundException(format("Item with itemId=%s is not found", itemId)));

        List<Booking> bookings = bookingRepository.findByBookerAndStatusOrderByStartDesc(author, APPROVED)
                                                  .stream()
                                                  .filter(booking -> booking.getItem() == item)
                                                  .filter(booking -> LocalDateTime.now().isAfter(booking.getEnd()))
                                                  .collect(Collectors.toList());

        if (bookings.size() > 0) {
            Comment comment = CommentMapper.toComment(commentDto, author, item);
            return CommentMapper.toCommentDto(commentRepository.save(comment));
        } else {
            throw new CommentNotAllowedException(format("User with usedId=%s cannot leave a comment for item with itemId=%s", userId, itemId));
        }
    }

    @Override
    public ItemDto patchItem(long itemId, long userId, Map<String, Object> fields) {
        Optional<Item> item = itemRepository.findById(itemId);
        Optional<User> owner = userRepository.findById(userId);

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

            return toItemDto(itemRepository.save(item.get()));
        } else {
            throw new ItemNotFoundException(format("Item with itemId=%s is not found", itemId));
        }
    }

    @Override
    public List<ItemDto> searchItem(String text, long userId) {
        if (text.isBlank()) {
            return Collections.emptyList();
        } else {
            return itemRepository.search(text)
                                 .stream().map(ItemMapper::toItemDto)
                                 .collect(Collectors.toList());
        }
    }

    @Override
    public List<ItemDto> searchItem(String text, long userId, int from, int size) {
        if (PageableUtils.isInvalidFromAndSize(from, size)) {
            throw new InvalidPageableParametersException("Invalid pageable parameters");
        }
        if (text.isBlank()) {
            return Collections.emptyList();
        } else {
            return itemRepository.search(text, PageRequest.of(from / size, size))
                                 .stream().map(ItemMapper::toItemDto)
                                 .collect(Collectors.toList());
        }
    }
}
