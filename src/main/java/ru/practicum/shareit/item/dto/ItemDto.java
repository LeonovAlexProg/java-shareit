package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.lang.Nullable;
import ru.practicum.shareit.item.constraints.ItemCreateConstraint;
import ru.practicum.shareit.item.constraints.ItemIdConstraint;
import ru.practicum.shareit.item.model.Item;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

@Data
@Builder
public class ItemDto {
    @Null(groups = ItemIdConstraint.class)
    private Long id;
    @Nullable
    private Long userId;
    @NotEmpty(groups = ItemCreateConstraint.class)
    @Nullable
    private String name;
    @NotEmpty(groups = ItemCreateConstraint.class)
    @Nullable
    private String description;
    @NotNull(groups = ItemCreateConstraint.class)
    @Nullable
    private Boolean available;

    public static ItemDto of(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .userId(item.getUser().getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getIsAvailable())
                .build();
    }
}
