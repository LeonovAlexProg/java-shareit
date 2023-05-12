package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import ru.practicum.shareit.item.constraints.ItemCreateConstraint;
import ru.practicum.shareit.item.constraints.ItemIdConstraint;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

@RequiredArgsConstructor
@Data
public class ItemDto {
    @Null(groups = ItemIdConstraint.class)
    private Integer id;
    @NotEmpty(groups = ItemCreateConstraint.class)
    @Nullable
    private String name;
    @NotEmpty(groups = ItemCreateConstraint.class)
    @Nullable
    private String description;
    @NotNull(groups = ItemCreateConstraint.class)
    @Nullable
    private Boolean available;
}
