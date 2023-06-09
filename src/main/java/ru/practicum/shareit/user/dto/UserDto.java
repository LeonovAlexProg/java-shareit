package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.lang.Nullable;
import ru.practicum.shareit.user.constraints.UserCreateConstraint;
import ru.practicum.shareit.user.constraints.UserIdConstraint;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class UserDto {
    @Null(groups = UserIdConstraint.class)
    private Long id;
    @Email(message = "Email not valid", groups = UserCreateConstraint.class)
    @NotNull(groups = UserCreateConstraint.class)
    @Nullable
    private String email;
    @NotNull(groups = UserCreateConstraint.class)
    @Nullable
    private String name;

    public static UserDto of(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }

    public static List<UserDto> listOf(List<User> userList) {
        return userList.stream().map(UserDto::of).collect(Collectors.toList());
    }
}