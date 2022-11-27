package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.validation.OnCreate;
import ru.practicum.shareit.validation.OnUpdate;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class UserDto {
    private long id;
    @NotNull(groups = OnCreate.class)
    private String name;
    @Email(groups = {OnCreate.class, OnUpdate.class})
    @NotNull(groups = OnCreate.class)
    private String email;
}
