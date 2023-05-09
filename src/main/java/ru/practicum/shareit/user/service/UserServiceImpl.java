package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.exception.EmailExistsException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<User> getAllUsers() {
        return userRepository.getAllUsers();
    }

    @Override
    public User createUser(User user) {
        validateUserEmail(user);

        return userRepository.addUser(user);
    }

    @Override
    public User getUser(int userId) {
        return userRepository.readUser(userId);
    }

    @Override
    public User patchUser(User user) {
        validateUserEmail(user);

        return userRepository.updateUser(user);
    }

    @Override
    public void deleteUser(int userId) {
        userRepository.deleteUser(userId);
    }

    private void validateUserEmail(User user) {
        if (getAllUsers().stream().anyMatch(curUser -> !Objects.equals(curUser.getId(), user.getId()) &&
                curUser.getEmail().equals(user.getEmail()))) {
            throw new EmailExistsException(String.format("User with %s email already exists", user.getEmail()));
        }
    }
}