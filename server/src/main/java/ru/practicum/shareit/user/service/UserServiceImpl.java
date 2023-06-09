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
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {
        User user = User.of(userDto);

        try {
            return UserMapper.userDtoOf(userRepository.save(user));
        } catch (Exception e) {
            throw new EmailExistsException("Email is already taken");
        }
    }

    @Override
    public List<UserDto> getAllUsers() {
        return UserMapper.userDtoListOf(userRepository.findAll());
    }


    @Override
    public UserDto getUser(Long userId) {
        return UserMapper.userDtoOf(userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("User id %d not found", userId))));
    }

    @Override
    @Transactional
    public UserDto patchUser(UserDto userDto) {
        User srcUser = User.of(userDto);
        User trgUser = userRepository.findById(userDto.getId())
                .orElseThrow(() -> new UserNotFoundException(String.format("User id %d not found", userDto.getId())));

        copyNonNullProperties(srcUser, trgUser);
        try {
            return UserMapper.userDtoOf(userRepository.save(trgUser));
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

    private static String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<>();
        for (java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) emptyNames.add(pd.getName());
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }
}