package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;
import ru.practicum.shareit.exceptions.EmailAlreadyExistsException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static ru.practicum.shareit.user.UserMapper.toUserDto;

@Service
public class UserServiceImpl implements UserService {
    private final Map<Long, String> uniqueEmails = new HashMap<>();
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDto getUserById(long userId) {
        return toUserDto(userRepository.getUserById(userId)
                                       .orElseThrow(() -> new UserNotFoundException(format("User with userId=%s is not found", userId)))
        );
    }

    @Override
    public List<UserDto> getUsers() {
        return userRepository.getAllUsers()
                             .stream()
                             .map(UserMapper::toUserDto)
                             .collect(Collectors.toList());
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        if (uniqueEmails.containsValue(userDto.getEmail())) {
            throw new EmailAlreadyExistsException(format("Email address %s already exists", userDto.getEmail()));
        }

        User user = UserMapper.toUser(userDto);
        user = userRepository.createUser(user);
        uniqueEmails.put(user.getId(), user.getEmail());
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto patchUser(long userId, Map<String, Object> fields) {
        Optional<User> user = userRepository.getUserById(userId);

        if (user.isPresent()) {
            fields.forEach((k, v) -> {
                Field field = ReflectionUtils.findField(User.class, k);
                if (field != null) {
                    if (k.equals("email")) {
                        if (!uniqueEmails.containsValue(v.toString())) {
                            uniqueEmails.put(user.get().getId(), v.toString());
                        } else {
                            throw new EmailAlreadyExistsException(format("Email address %s already exists", v));
                        }
                    }
                    field.setAccessible(true);
                    ReflectionUtils.setField(field, user.get(), v);
                }
            });
            userRepository.updateUser(user.get());
            return toUserDto(user.get());
        } else {
            throw new UserNotFoundException(format("User with userId=%s is not found", userId));
        }
    }

    @Override
    public void deleteUser(long userId) {
        userRepository.deleteUser(userId);
        uniqueEmails.remove(userId);
    }
}
