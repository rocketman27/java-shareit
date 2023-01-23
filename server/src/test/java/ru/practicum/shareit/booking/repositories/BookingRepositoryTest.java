package ru.practicum.shareit.booking.repositories;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repositories.ItemRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
class BookingRepositoryTest {
    @Autowired
    private TestEntityManager em;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;

    @Test
    void contextLoads() {
        Assertions.assertNotNull(em);
        Assertions.assertNotNull(bookingRepository);
        Assertions.assertNotNull(userRepository);
        Assertions.assertNotNull(itemRepository);
    }

    @Test
    void findPastBookingsByBookerTest() {
        User booker = User.builder()
                          .withName("User")
                          .withEmail("user_email@gmail.com")
                          .build();

        booker = userRepository.save(booker);

        Item item = Item.builder()
                        .withName("Item")
                        .withDescription("Description")
                        .withAvailable(true)
                        .build();

        item = itemRepository.save(item);

        LocalDateTime start = LocalDateTime.now().minusMinutes(2);
        LocalDateTime end = LocalDateTime.now().minusMinutes(1);

        Booking booking = Booking.builder()
                                 .withBooker(booker)
                                 .withStart(start)
                                 .withEnd(end)
                                 .withItem(item)
                                 .withStatus(BookingStatus.APPROVED)
                                 .build();

        booking = bookingRepository.save(booking);
        List<Booking> pastBookings = bookingRepository.findPastBookingsByBooker(booker);
        Assertions.assertEquals(1, pastBookings.size());
        Assertions.assertEquals(booking, pastBookings.get(0));
    }

    @Test
    void findPastBookingsByOwnerTest() {
        User booker = User.builder()
                          .withName("Booker")
                          .withEmail("booker_email@gmail.com")
                          .build();

        User owner = User.builder()
                         .withName("User")
                         .withEmail("owner_email@gmail.com")
                         .build();

        owner = userRepository.save(owner);
        booker = userRepository.save(booker);

        Item item = Item.builder()
                        .withName("Item")
                        .withDescription("Description")
                        .withOwner(owner)
                        .withAvailable(true)
                        .build();

        item = itemRepository.save(item);

        LocalDateTime start = LocalDateTime.now().minusMinutes(2);
        LocalDateTime end = LocalDateTime.now().minusMinutes(1);

        Booking booking = Booking.builder()
                                 .withBooker(booker)
                                 .withStart(start)
                                 .withEnd(end)
                                 .withItem(item)
                                 .withStatus(BookingStatus.APPROVED)
                                 .build();

        booking = bookingRepository.save(booking);
        List<Booking> pastBookings = bookingRepository.findPastBookingsByOwner(owner);
        Assertions.assertEquals(1, pastBookings.size());
        Assertions.assertEquals(booking, pastBookings.get(0));
    }

    @Test
    void findFutureBookingsByBookerTest() {
        User booker = User.builder()
                          .withName("User")
                          .withEmail("user_email@gmail.com")
                          .build();

        booker = userRepository.save(booker);

        Item item = Item.builder()
                        .withName("Item")
                        .withDescription("Description")
                        .withAvailable(true)
                        .build();

        item = itemRepository.save(item);

        LocalDateTime start = LocalDateTime.now().plusMinutes(1);
        LocalDateTime end = LocalDateTime.now().plusMinutes(2);

        Booking booking = Booking.builder()
                                 .withBooker(booker)
                                 .withStart(start)
                                 .withEnd(end)
                                 .withItem(item)
                                 .withStatus(BookingStatus.APPROVED)
                                 .build();

        booking = bookingRepository.save(booking);
        List<Booking> futureBookings = bookingRepository.findFutureBookingsByBooker(booker);
        Assertions.assertEquals(1, futureBookings.size());
        Assertions.assertEquals(booking, futureBookings.get(0));
    }

    @Test
    void findFutureBookingsByOwnerTest() {
        User booker = User.builder()
                          .withName("User")
                          .withEmail("user_email@gmail.com")
                          .build();

        booker = userRepository.save(booker);

        User owner = User.builder()
                         .withName("User")
                         .withEmail("owner_email@gmail.com")
                         .build();

        owner = userRepository.save(owner);

        Item item = Item.builder()
                        .withName("Item")
                        .withDescription("Description")
                        .withOwner(owner)
                        .withAvailable(true)
                        .build();

        item = itemRepository.save(item);

        LocalDateTime start = LocalDateTime.now().plusMinutes(1);
        LocalDateTime end = LocalDateTime.now().plusMinutes(2);

        Booking booking = Booking.builder()
                                 .withBooker(booker)
                                 .withStart(start)
                                 .withEnd(end)
                                 .withItem(item)
                                 .withStatus(BookingStatus.APPROVED)
                                 .build();

        booking = bookingRepository.save(booking);
        List<Booking> futureBookings = bookingRepository.findFutureBookingsByOwner(owner);
        Assertions.assertEquals(1, futureBookings.size());
        Assertions.assertEquals(booking, futureBookings.get(0));
    }

    @Test
    void findCurrentBookingsByBookerTest() {
        User booker = User.builder()
                          .withName("User")
                          .withEmail("user_email@gmail.com")
                          .build();

        booker = userRepository.save(booker);

        Item item = Item.builder()
                        .withName("Item")
                        .withDescription("Description")
                        .withAvailable(true)
                        .build();

        item = itemRepository.save(item);

        LocalDateTime start = LocalDateTime.now().minusMinutes(1);
        LocalDateTime end = LocalDateTime.now().plusMinutes(1);

        Booking booking = Booking.builder()
                                 .withBooker(booker)
                                 .withStart(start)
                                 .withEnd(end)
                                 .withItem(item)
                                 .withStatus(BookingStatus.APPROVED)
                                 .build();

        booking = bookingRepository.save(booking);
        List<Booking> futureBookings = bookingRepository.findCurrentBookingsByBooker(booker);
        Assertions.assertEquals(1, futureBookings.size());
        Assertions.assertEquals(booking, futureBookings.get(0));
    }

    @Test
    void findCurrentBookingsByOwnerTest() {
        User booker = User.builder()
                          .withName("User")
                          .withEmail("user_email@gmail.com")
                          .build();

        booker = userRepository.save(booker);

        User owner = User.builder()
                         .withName("User")
                         .withEmail("owner_email@gmail.com")
                         .build();

        owner = userRepository.save(owner);

        Item item = Item.builder()
                        .withName("Item")
                        .withDescription("Description")
                        .withOwner(owner)
                        .withAvailable(true)
                        .build();

        item = itemRepository.save(item);

        LocalDateTime start = LocalDateTime.now().minusMinutes(1);
        LocalDateTime end = LocalDateTime.now().plusMinutes(1);

        Booking booking = Booking.builder()
                                 .withBooker(booker)
                                 .withStart(start)
                                 .withEnd(end)
                                 .withItem(item)
                                 .withStatus(BookingStatus.APPROVED)
                                 .build();

        booking = bookingRepository.save(booking);
        List<Booking> futureBookings = bookingRepository.findCurrentBookingsByOwner(owner);
        Assertions.assertEquals(1, futureBookings.size());
        Assertions.assertEquals(booking, futureBookings.get(0));
    }

    @Test
    void findByBookerOrderByStartDescTest() {
        User booker = User.builder()
                          .withName("User")
                          .withEmail("user_email@gmail.com")
                          .build();

        booker = userRepository.save(booker);

        Item item = Item.builder()
                        .withName("Item")
                        .withDescription("Description")
                        .withAvailable(true)
                        .build();

        item = itemRepository.save(item);

        LocalDateTime start = LocalDateTime.now().plusMinutes(1);
        LocalDateTime end = LocalDateTime.now().plusMinutes(2);

        Booking booking = Booking.builder()
                                 .withBooker(booker)
                                 .withStart(start)
                                 .withEnd(end)
                                 .withItem(item)
                                 .withStatus(BookingStatus.APPROVED)
                                 .build();

        booking = bookingRepository.save(booking);
        List<Booking> futureBookings = bookingRepository.findByBookerOrderByStartDesc(booker);
        Assertions.assertEquals(1, futureBookings.size());
        Assertions.assertEquals(booking, futureBookings.get(0));
    }

    @Test
    void findByOwnerOrderByStartDescTest() {
        User booker = User.builder()
                          .withName("User")
                          .withEmail("user_email@gmail.com")
                          .build();

        booker = userRepository.save(booker);

        User owner = User.builder()
                         .withName("User")
                         .withEmail("owner_email@gmail.com")
                         .build();

        owner = userRepository.save(owner);

        Item item = Item.builder()
                        .withName("Item")
                        .withDescription("Description")
                        .withOwner(owner)
                        .withAvailable(true)
                        .build();

        item = itemRepository.save(item);

        LocalDateTime start = LocalDateTime.now().plusMinutes(1);
        LocalDateTime end = LocalDateTime.now().plusMinutes(2);

        Booking booking = Booking.builder()
                                 .withBooker(booker)
                                 .withStart(start)
                                 .withEnd(end)
                                 .withItem(item)
                                 .withStatus(BookingStatus.APPROVED)
                                 .build();

        booking = bookingRepository.save(booking);
        List<Booking> futureBookings = bookingRepository.findFutureBookingsByOwner(owner);
        Assertions.assertEquals(1, futureBookings.size());
        Assertions.assertEquals(booking, futureBookings.get(0));
    }

    @Test
    void findByBookerAndStatusOrderByStartDescTest() {
        User booker = User.builder()
                          .withName("User")
                          .withEmail("user_email@gmail.com")
                          .build();

        booker = userRepository.save(booker);

        Item item = Item.builder()
                        .withName("Item")
                        .withDescription("Description")
                        .withAvailable(true)
                        .build();

        item = itemRepository.save(item);

        LocalDateTime start = LocalDateTime.now().plusMinutes(1);
        LocalDateTime end = LocalDateTime.now().plusMinutes(2);

        Booking booking = Booking.builder()
                                 .withBooker(booker)
                                 .withStart(start)
                                 .withEnd(end)
                                 .withItem(item)
                                 .withStatus(BookingStatus.REJECTED)
                                 .build();

        booking = bookingRepository.save(booking);
        List<Booking> futureBookings = bookingRepository.findByBookerAndStatusOrderByStartDesc(booker, BookingStatus.REJECTED);
        Assertions.assertEquals(1, futureBookings.size());
        Assertions.assertEquals(booking, futureBookings.get(0));
    }

    @Test
    void findByOwnerAndStatusOrderByStartDescTest() {
        User booker = User.builder()
                          .withName("User")
                          .withEmail("user_email@gmail.com")
                          .build();

        booker = userRepository.save(booker);

        User owner = User.builder()
                         .withName("User")
                         .withEmail("owner_email@gmail.com")
                         .build();

        owner = userRepository.save(owner);

        Item item = Item.builder()
                        .withName("Item")
                        .withDescription("Description")
                        .withOwner(owner)
                        .withAvailable(true)
                        .build();

        item = itemRepository.save(item);

        LocalDateTime start = LocalDateTime.now().plusMinutes(1);
        LocalDateTime end = LocalDateTime.now().plusMinutes(2);

        Booking booking = Booking.builder()
                                 .withBooker(booker)
                                 .withStart(start)
                                 .withEnd(end)
                                 .withItem(item)
                                 .withStatus(BookingStatus.WAITING)
                                 .build();

        booking = bookingRepository.save(booking);
        List<Booking> futureBookings = bookingRepository.findByOwnerAndStatusOrderByStartDesc(owner, BookingStatus.WAITING);
        Assertions.assertEquals(1, futureBookings.size());
        Assertions.assertEquals(booking, futureBookings.get(0));
    }

    @Test
    void findByItemIdAndOwnerIdOrderByStartDescTest() {
        User booker = User.builder()
                          .withName("User")
                          .withEmail("user_email@gmail.com")
                          .build();

        booker = userRepository.save(booker);

        User owner = User.builder()
                         .withName("User")
                         .withEmail("owner_email@gmail.com")
                         .build();

        owner = userRepository.save(owner);

        Item item = Item.builder()
                        .withName("Item")
                        .withDescription("Description")
                        .withOwner(owner)
                        .withAvailable(true)
                        .build();

        item = itemRepository.save(item);

        LocalDateTime start = LocalDateTime.now().plusMinutes(1);
        LocalDateTime end = LocalDateTime.now().plusMinutes(2);

        Booking booking = Booking.builder()
                                 .withBooker(booker)
                                 .withStart(start)
                                 .withEnd(end)
                                 .withItem(item)
                                 .withStatus(BookingStatus.WAITING)
                                 .build();

        booking = bookingRepository.save(booking);
        List<Booking> futureBookings = bookingRepository.findByItemIdAndOwnerIdOrderByStartDesc(item.getId(), owner.getId());
        Assertions.assertEquals(1, futureBookings.size());
        Assertions.assertEquals(booking, futureBookings.get(0));
    }
}