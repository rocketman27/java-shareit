package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.validation.ValidEndDate;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder(setterPrefix = "with")
@ValidEndDate
public class BookingDto {
    private long id;
    @NotNull
    private long itemId;
    @NotNull
    @FutureOrPresent
    private LocalDateTime start;
    @NotNull
    @Future
    private LocalDateTime end;
    private Item item;
    private Booker booker;
    private BookingStatus status;

    @Data
    @Builder(setterPrefix = "with")
    public static class Item {
        private long id;
        private String name;
    }

    @Data
    @Builder(setterPrefix = "with")
    public static class Booker {
        private long id;
    }
}
