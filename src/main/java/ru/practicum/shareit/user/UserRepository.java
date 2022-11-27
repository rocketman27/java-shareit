package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
public class UserRepository {
    private final Map<Long, User> users = new HashMap<>();

    public Optional<User> getUserById(long userId) {
        return Optional.ofNullable(users.get(userId));
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    public User createUser(User user) {
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
