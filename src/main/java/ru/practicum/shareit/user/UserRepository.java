package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private static long nextId = 1;


    public Optional<User> getUserById(long userId) {
        return Optional.ofNullable(users.get(userId));
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    public User createUser(User user) {
        user.setId(nextId++);
        users.put(user.getId(), user);
        return user;
    }

    public void updateUser(User user) {
        users.put(user.getId(), user);
    }

    public void deleteUser(long userId) {
        users.remove(userId);
    }
}
