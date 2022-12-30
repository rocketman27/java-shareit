package ru.practicum.shareit.booking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.mappers.BookingMapper;
import ru.practicum.shareit.booking.repositories.BookingRepository;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exceptions.ActionNotAllowedException;
import ru.practicum.shareit.exceptions.BookingAlreadyApproved;
import ru.practicum.shareit.exceptions.BookingNotFoundException;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.ItemUnavailableException;
import ru.practicum.shareit.exceptions.UnsupportedStateException;
import ru.practicum.shareit.exceptions.UserMismatchException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.repositories.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.utils.EnumUtils;

import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static ru.practicum.shareit.booking.mappers.BookingMapper.toBooking;
import static ru.practicum.shareit.booking.mappers.BookingMapper.toBookingDto;
import static ru.practicum.shareit.booking.model.BookingStatus.*;

@Service
public class BookingServiceImpl implements BookingService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    @Autowired
    public BookingServiceImpl(ItemRepository itemRepository,
                              UserRepository userRepository,
                              BookingRepository bookingRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    public BookingDto approveOrRejectBooking(long bookingId, boolean approved, long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                                           .orElseThrow(() -> new BookingNotFoundException(
                                                   format("Booking with bookingId=%s is not found", bookingId))
                                           );

        if (booking.getBooker().getId() == userId) {
            throw new ActionNotAllowedException(
                    format("Booker with userId=%s is not allowed to approve his(her) booking requests", userId)
            );
        }

        if (booking.getStatus().equals(APPROVED)) {
            throw new BookingAlreadyApproved(format("Booking with bookingId=%s is already approved", bookingId));
        }

        if (booking.getItem().getOwner().getId() == userId) {
            if (approved) {
                booking.setStatus(APPROVED);
            } else {
                booking.setStatus(REJECTED);
            }
            return toBookingDto(bookingRepository.save(booking));
        } else {
            throw new UserMismatchException(format("User with userId=%s is not allowed to change the status of the booking", userId));
        }
    }

    @Override
    public BookingDto getBookingById(long bookingId, long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                                           .orElseThrow(() -> new BookingNotFoundException(
                                                   format("Booking with bookingId=%s is not found", bookingId))
                                           );

        if (booking.getBooker().getId() != userId && booking.getItem().getOwner().getId() != userId) {
            throw new BookingNotFoundException(
                    format("Booking with bookingId=%s doesn't belong to user with userId=%s", bookingId, userId)
            );
        }

        return toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getAllBookingsByBooker(String state, long userId) {
        User booker = userRepository.findById(userId)
                                    .orElseThrow(() -> new UserNotFoundException(
                                            format("Booker with userId=%s is not found", userId))
                                    );

        return getBookingsByBooker(state, booker);
    }

    private List<BookingDto> getBookingsByBooker(String state, User booker) {
        BookingState bookingState = EnumUtils.findEnumValue(BookingState.class, state);
        List<Booking> bookings;

        switch (bookingState) {
            case ALL:
                bookings = bookingRepository.findByBookerOrderByStartDesc(booker);
                break;
            case PAST:
                bookings = bookingRepository.findPastBookingsByBooker(booker);
                break;
            case FUTURE:
                bookings = bookingRepository.findFutureBookingsByBooker(booker);
                break;
            case CURRENT:
                bookings = bookingRepository.findCurrentBookingsByBooker(booker);
                break;
            case WAITING:
                bookings = bookingRepository.findByBookerAndStatusOrderByStartDesc(booker, WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findByBookerAndStatusOrderByStartDesc(booker, REJECTED);
                break;
            default:
                throw new UnsupportedStateException("Unknown state: UNSUPPORTED_STATUS");
        }

        return bookings.stream()
                       .map(BookingMapper::toBookingDto)
                       .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getAllBookingsByOwner(String state, long userId) {
        User owner = userRepository.findById(userId)
                                    .orElseThrow(() -> new UserNotFoundException(
                                            format("Owner with userId=%s is not found", userId))
                                    );

        return getBookingsByOwner(state, owner);
    }

    private List<BookingDto> getBookingsByOwner(String state, User owner) {
        BookingState bookingState = EnumUtils.findEnumValue(BookingState.class, state);
        List<Booking> bookings;

        switch (bookingState) {
            case ALL:
                bookings = bookingRepository.findByOwnerOrderByStartDesc(owner);
                break;
            case PAST:
                bookings = bookingRepository.findPastBookingsByOwner(owner);
                break;
            case FUTURE:
                bookings = bookingRepository.findFutureBookingsByOwner(owner);
                break;
            case CURRENT:
                bookings = bookingRepository.findCurrentBookingsByOwner(owner);
                break;
            case WAITING:
                bookings = bookingRepository.findByOwnerAndStatusOrderByStartDesc(owner, WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findByOwnerAndStatusOrderByStartDesc(owner, REJECTED);
                break;
            default:
                throw new UnsupportedStateException("Unknown state: UNSUPPORTED_STATUS");
        }

        return bookings.stream()
                       .map(BookingMapper::toBookingDto)
                       .collect(Collectors.toList());
    }

    @Override
    public BookingDto createBooking(long userId, BookingDto bookingDto) {
        User user = userRepository.findById(userId)
                                  .orElseThrow(() -> new UserNotFoundException(
                                          format("Owner with userId=%s is not found", userId))
                                  );

        Item item = itemRepository.findById(bookingDto.getItemId())
                                  .orElseThrow(() -> new ItemNotFoundException(
                                          format("Item with itemId=%s is not found", bookingDto.getItemId()))
                                  );

        if (item.getOwner().getId() == userId) {
            throw new ActionNotAllowedException(
                    format("Owner with userId=%s of the item with itemId=%s is not allowed to book it", userId, item.getId())
            );
        }

        if (item.getAvailable()) {
            Booking booking = toBooking(user, item, bookingDto);
            booking.setStatus(WAITING);
            return toBookingDto(bookingRepository.save(booking));
        } else {
            throw new ItemUnavailableException(format("Item with itemId=%s is unavailable for booking", item.getId()));
        }
    }
}
