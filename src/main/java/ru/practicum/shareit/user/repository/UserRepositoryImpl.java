package ru.practicum.shareit.user.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private final Map<Integer, User> users;
    private int id = 0;

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User addUser(User user) {
        id++;
        user.setId(id);
        users.put(id, user);
        return user;
    }

    @Override
    public User readUser(int userId) {
        if (!users.containsKey(userId)) {
            log.debug("User id {} not found", userId);
            throw new UserNotFoundException(String.format("User id %d not found", userId));
        }
        return users.get(userId);
    }

    @Override
    public User updateUser(User user) {
        User inMemoryUser = users.get(user.getId());

        if (user.getEmail() != null) {
            inMemoryUser.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            inMemoryUser.setName(user.getName());
        }

        return inMemoryUser;
    }

    @Override
    public void deleteUser(int userId) {
        if (!users.containsKey(userId)) {
            log.debug("User id {} not found", userId);
            throw new UserNotFoundException(String.format("User id %d not found", userId));
        }
        users.remove(userId);
    }
}
