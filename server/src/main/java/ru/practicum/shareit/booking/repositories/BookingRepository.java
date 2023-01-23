package ru.practicum.shareit.booking.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Validated
public interface BookingRepository extends PagingAndSortingRepository<Booking, Long> {

    @Query("select b from Booking b " +
            "where b.booker = ?1 and b.end < current_timestamp " +
            "order by b.start desc ")
    List<Booking> findPastBookingsByBooker(User booker);

    @Query("select b from Booking b " +
            "where b.booker = ?1 and b.end < current_timestamp " +
            "order by b.start desc ")
    Page<Booking> findPastBookingsByBooker(User booker, Pageable pageable);

    @Query("select b from Booking b left join b.item i " +
            "where i.owner = ?1 and b.end < current_timestamp " +
            "order by b.start desc ")
    List<Booking> findPastBookingsByOwner(User owner);

    @Query("select b from Booking b left join b.item i " +
            "where i.owner = ?1 and b.end < current_timestamp " +
            "order by b.start desc ")
    Page<Booking> findPastBookingsByOwner(User owner, Pageable pageable);

    @Query("select b from Booking b " +
            "where b.booker = ?1 and b.start > current_timestamp " +
            "order by b.start desc ")
    List<Booking> findFutureBookingsByBooker(User booker);

    @Query("select b from Booking b " +
            "where b.booker = ?1 and b.start > current_timestamp " +
            "order by b.start desc ")
    Page<Booking> findFutureBookingsByBooker(User booker, Pageable pageable);

    @Query("select b from Booking b left join b.item i " +
            "where i.owner = ?1 and b.start > current_timestamp " +
            "order by b.start desc ")
    List<Booking> findFutureBookingsByOwner(User owner);

    @Query("select b from Booking b left join b.item i " +
            "where i.owner = ?1 and b.start > current_timestamp " +
            "order by b.start desc ")
    Page<Booking> findFutureBookingsByOwner(User owner, Pageable pageable);

    @Query("select b from Booking b " +
            "where b.booker = ?1 and b.start < current_timestamp and b.end > current_timestamp " +
            "order by b.start desc ")
    List<Booking> findCurrentBookingsByBooker(User booker);

    @Query("select b from Booking b " +
            "where b.booker = ?1 and b.start < current_timestamp and b.end > current_timestamp " +
            "order by b.start desc ")
    Page<Booking> findCurrentBookingsByBooker(User booker, Pageable pageable);

    @Query("select b from Booking b left join b.item i " +
            "where i.owner = ?1 and b.start < current_timestamp and b.end > current_timestamp " +
            "order by b.start desc ")
    List<Booking> findCurrentBookingsByOwner(User owner);

    @Query("select b from Booking b left join b.item i " +
            "where i.owner = ?1 and b.start < current_timestamp and b.end > current_timestamp " +
            "order by b.start desc ")
    Page<Booking> findCurrentBookingsByOwner(User owner, Pageable pageable);

    List<Booking> findByBookerOrderByStartDesc(User booker);

    Page<Booking> findByBookerOrderByStartDesc(User booker, Pageable pageable);

    @Query("select b from Booking b left join b.item i " +
            "where i.owner = ?1 " +
            "order by b.start desc ")
    List<Booking> findByOwnerOrderByStartDesc(User owner);

    @Query("select b from Booking b left join b.item i " +
            "where i.owner = ?1 " +
            "order by b.start desc ")
    Page<Booking> findByOwnerOrderByStartDesc(User owner, Pageable pageable);

    List<Booking> findByBookerAndStatusOrderByStartDesc(User booker, BookingStatus status);

    Page<Booking> findByBookerAndStatusOrderByStartDesc(User booker, BookingStatus status, Pageable pageable);

    @Query("select b from Booking b left join b.item i " +
            "where i.owner = ?1 and b.status = ?2 " +
            "order by b.start desc ")
    List<Booking> findByOwnerAndStatusOrderByStartDesc(User owner, BookingStatus status);

    @Query("select b from Booking b left join b.item i " +
            "where i.owner = ?1 and b.status = ?2 " +
            "order by b.start desc ")
    Page<Booking> findByOwnerAndStatusOrderByStartDesc(User owner, BookingStatus status, Pageable pageable);

    @Query("select b from Booking b left join b.item i " +
            "where b.item.id = ?1 and i.owner.id = ?2 " +
            "order by b.start desc ")
    List<Booking> findByItemIdAndOwnerIdOrderByStartDesc(long itemId, long ownerId);
}
