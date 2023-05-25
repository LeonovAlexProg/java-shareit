package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {
    List<User> getAllUsers();

    User addUser(User user);

    User readUser(int userId);

    User updateUser(User user);

    void deleteUser(int userId);
}
