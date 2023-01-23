package ru.practicum.shareit.request.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder(setterPrefix = "with")
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestDto {
    private long id;
    private String description;
    private LocalDateTime created;
    private List<Item> items;

    @Data
    @Builder(setterPrefix = "with")
    public static class Item {
        private long id;
        private String name;
        private String description;
        private boolean isAvailable;
        private long requestId;
    }
}
