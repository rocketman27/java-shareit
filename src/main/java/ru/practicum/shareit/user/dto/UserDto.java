package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.validation.OnCreate;
import ru.practicum.shareit.validation.OnUpdate;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(setterPrefix = "with")
public class UserDto {
    @NotNull(groups = OnUpdate.class)
    private long id;
    @NotNull(groups = OnCreate.class)
    private String name;
    @Email(groups = {OnCreate.class, OnUpdate.class})
    @NotNull(groups = OnCreate.class)
    private String email;
}
