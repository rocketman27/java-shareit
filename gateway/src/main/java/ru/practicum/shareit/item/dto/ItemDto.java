package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.validation.OnCreate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder(setterPrefix = "with")
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    private long id;
    @NotNull(groups = OnCreate.class)
    @NotBlank(groups = OnCreate.class)
    private String name;
    @NotNull(groups = OnCreate.class)
    @NotBlank(groups = OnCreate.class)
    private String description;
    @NotNull(groups = OnCreate.class)
    private Boolean available;
    private Booking lastBooking;
    private Booking nextBooking;
    private List<Comment> comments;
    private long requestId;

    @Data
    @Builder(setterPrefix = "with")
    public static class Booking {
        private long id;
        private LocalDateTime start;
        private LocalDateTime end;
        private long bookerId;
    }

    @Data
    @Builder(setterPrefix = "with")
    public static class Comment {
        private long id;
        private String text;
        private String authorName;
        private LocalDateTime created;
    }
}
