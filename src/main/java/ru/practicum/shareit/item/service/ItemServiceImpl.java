package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDetailsDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exceptions.CommentNotAllowedException;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.UserMismatchException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mappers.CommentMapper;
import ru.practicum.shareit.item.mappers.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repositories.CommentRepository;
import ru.practicum.shareit.item.repositories.ItemRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

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

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository,
                           BookingRepository bookingRepository, CommentRepository commentRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    public ItemDto getItemById(long userId, long itemId) {
        List<BookingDetailsDto> bookings = bookingRepository.findByItemIdAndOwnerIdOrderByStartDesc(itemId, userId);
        LocalDateTime currentTime = LocalDateTime.now();
        Optional<BookingDetailsDto> lastBooking = getLastBooking(currentTime, bookings);
        Optional<BookingDetailsDto> nextBooking = getNextBooking(currentTime, bookings);

        List<CommentDto> comments = commentRepository.findByItemId(itemId)
                                                     .stream()
                                                     .map(CommentMapper::toCommentDto)
                                                     .collect(Collectors.toList());

        Item item = itemRepository.findById(itemId)
                                  .orElseThrow(() -> new ItemNotFoundException(format("Item with itemId=%s is not found", itemId)));

        ItemDto itemDto = toItemDto(item);

        lastBooking.ifPresent(itemDto::setLastBooking);
        nextBooking.ifPresent(itemDto::setNextBooking);
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
                                 List<CommentDto> comments = commentRepository.findByAuthorIdAndItemId(userId, item.getId())
                                                                              .stream()
                                                                              .map(CommentMapper::toCommentDto)
                                                                              .collect(Collectors.toList());
                                 itemDto.setComments(comments);

                                 List<BookingDetailsDto> bookings = bookingRepository.findByItemIdAndOwnerIdOrderByStartDesc(item.getId(), userId);
                                 Optional<BookingDetailsDto> lastBooking = getLastBooking(currentTime, bookings);
                                 Optional<BookingDetailsDto> nextBooking = getNextBooking(currentTime, bookings);
                                 lastBooking.ifPresent(itemDto::setLastBooking);
                                 nextBooking.ifPresent(itemDto::setNextBooking);
                                 return itemDto;
                             })
                             .collect(Collectors.toList());
    }

    private static Optional<BookingDetailsDto> getLastBooking(LocalDateTime currentTime,
                                                              List<BookingDetailsDto> bookings) {
        return bookings.stream()
                       .filter(booking -> booking.getEnd().isBefore(currentTime))
                       .limit(1)
                       .findFirst();
    }

    private static Optional<BookingDetailsDto> getNextBooking(LocalDateTime currentTime,
                                                              List<BookingDetailsDto> bookings) {
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
            return itemRepository.findAll()
                                 .stream()
                                 .filter(Item::getAvailable)
                                 .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase()) ||
                                         item.getDescription().toLowerCase().contains(text.toLowerCase()))
                                 .map(ItemMapper::toItemDto)
                                 .collect(Collectors.toList());
        }
    }
}
