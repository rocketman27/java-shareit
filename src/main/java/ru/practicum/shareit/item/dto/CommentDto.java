package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.validation.OnCreate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder(setterPrefix = "with")
public class CommentDto {
    private long id;
    @NotNull(groups = OnCreate.class)
    @NotBlank(groups = OnCreate.class)
    private String text;
    private String authorName;
    private LocalDateTime created;
}
