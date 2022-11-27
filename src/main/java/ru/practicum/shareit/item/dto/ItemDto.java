package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.validation.OnCreate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder(setterPrefix = "with")
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
}
