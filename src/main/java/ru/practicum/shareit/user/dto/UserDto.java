package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class UserDto {
    @Email(message = "Email not valid")
    @NotNull
    private String email;
    @NotNull
    private String name;
}