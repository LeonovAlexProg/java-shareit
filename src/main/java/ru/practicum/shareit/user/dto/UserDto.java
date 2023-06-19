package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.lang.Nullable;
import ru.practicum.shareit.user.constraints.UserCreateConstraint;
import ru.practicum.shareit.user.constraints.UserIdConstraint;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

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
}