package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Map;

public interface UserService {

    UserDto getUserById(long userId);

    List<UserDto> getUsers();

    UserDto createUser(UserDto userDto);

    UserDto patchUser(long userId, Map<String, Object> fields);

    void deleteUser(long userId);

}
