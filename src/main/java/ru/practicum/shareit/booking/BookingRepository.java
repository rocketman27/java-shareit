package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.dto.BookingDetailsDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("select b from Booking b " +
            "where b.booker = ?1 and b.end < current_timestamp " +
            "order by b.start desc ")
    List<Booking> findPastBookingsByBooker(User booker);

    @Query("select b from Booking b left join b.item i " +
            "where i.owner = ?1 and b.end < current_timestamp " +
            "order by b.start desc ")
    List<Booking> findPastBookingsByOwner(User owner);

    @Query("select b from Booking b " +
            "where b.booker = ?1 and b.start > current_timestamp " +
            "order by b.start desc ")
    List<Booking> findFutureBookingsByBooker(User booker);

    @Query("select b from Booking b left join b.item i " +
            "where i.owner = ?1 and b.start > current_timestamp " +
            "order by b.start desc ")
    List<Booking> findFutureBookingsByOwner(User owner);

    @Query("select b from Booking b " +
            "where b.booker = ?1 and b.start < current_timestamp and b.end > current_timestamp " +
            "order by b.start desc ")
    List<Booking> findCurrentBookingsByBooker(User booker);

    @Query("select b from Booking b left join b.item i " +
            "where i.owner = ?1 and b.start < current_timestamp and b.end > current_timestamp " +
            "order by b.start desc ")
    List<Booking> findCurrentBookingsByOwner(User owner);

    List<Booking> findByBookerOrderByStartDesc(User booker);

    @Query("select b from Booking b left join b.item i " +
            "where i.owner = ?1 " +
            "order by b.start desc ")
    List<Booking> findByOwnerOrderByStartDesc(User owner);

    List<Booking> findByBookerAndStatusOrderByStartDesc(User booker, BookingStatus status);

    @Query("select b from Booking b left join b.item i " +
            "where i.owner = ?1 and b.status = ?2 " +
            "order by b.start desc ")
    List<Booking> findByOwnerAndStatusOrderByStartDesc(User owner, BookingStatus status);

    @Query("select new ru.practicum.shareit.booking.dto.BookingDetailsDto(b.id, b.start, b.end, b.booker.id) " +
            "from Booking b left join b.item i " +
            "where b.item.id = ?1 and i.owner.id = ?2 " +
            "order by b.start desc ")
    List<BookingDetailsDto> findByItemIdAndOwnerIdOrderByStartDesc(long itemId, long ownerId);
}
