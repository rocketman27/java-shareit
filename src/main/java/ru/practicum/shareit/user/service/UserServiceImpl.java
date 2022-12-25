package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static ru.practicum.shareit.user.dto.UserMapper.toUserDto;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDto getUserById(long userId) {
        return toUserDto(userRepository.findById(userId)
                                       .orElseThrow(() -> new UserNotFoundException(format("User with userId=%s is not found", userId)))
        );
    }

    @Override
    public List<UserDto> getUsers() {
        return userRepository.findAll()
                             .stream()
                             .map(UserMapper::toUserDto)
                             .collect(Collectors.toList());
    }

@Override
    public UserDto createUser(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public UserDto patchUser(long userId, Map<String, Object> fields) {
        Optional<User> user = userRepository.findById(userId);

        if (user.isPresent()) {
            fields.forEach((k, v) -> {
                Field field = ReflectionUtils.findField(User.class, k);
                if (field != null) {
                    field.setAccessible(true);
                    ReflectionUtils.setField(field, user.get(), v);
                }
            });
            return toUserDto(userRepository.save(user.get()));
        } else {
            throw new UserNotFoundException(format("User with userId=%s is not found", userId));
        }
    }

    @Override
    public void deleteUser(long userId) {
        userRepository.deleteById(userId);
    }
}
