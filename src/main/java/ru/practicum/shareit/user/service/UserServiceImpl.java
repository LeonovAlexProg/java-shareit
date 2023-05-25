package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exceptions.EmailExistsException;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<UserDto> getAllUsers() {
        return UserDto.listOf(userRepository.findAll());
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = User.of(userDto);

        try {
            return UserDto.of(userRepository.save(user));
        } catch (Exception e) {
            throw new EmailExistsException("Email is already taken");
        }
    }

    @Override
    public UserDto getUser(Long userId) {
        return UserDto.of(userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("User id %d not found", userId))));
    }

    @Override
    public UserDto patchUser(UserDto userDto) {
        User srcUser = User.of(userDto);
        User trgUser = userRepository.findById(userDto.getId())
                .orElseThrow(() -> new UserNotFoundException(String.format("User id %d not found", userDto.getId())));

        copyNonNullProperties(srcUser, trgUser);
        try {
            return UserDto.of(userRepository.save(trgUser));
        } catch (Exception e) {
            throw new EmailExistsException("Email is already taken");
        }
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    private static void copyNonNullProperties(Object src, Object target) {
        BeanUtils.copyProperties(src, target, getNullPropertyNames(src));
    }

    private static String[] getNullPropertyNames (Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<String>();
        for(java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) emptyNames.add(pd.getName());
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }
}