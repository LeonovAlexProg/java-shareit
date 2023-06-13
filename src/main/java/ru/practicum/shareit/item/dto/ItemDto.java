package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.lang.Nullable;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.constraints.ItemCreateConstraint;
import ru.practicum.shareit.item.constraints.ItemIdConstraint;
import ru.practicum.shareit.item.model.Item;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.List;
import java.util.stream.Collectors;

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
    @Nullable
    private BookingShortDto lastBooking;
    @Nullable
    private BookingShortDto nextBooking;
    @Nullable
    private List<CommentResponseDto> comments;
    @Nullable
    private Long requestId;

    public static ItemDto of(Item item) {
        ItemDto dto = ItemDto.builder()
                .id(item.getId())
                .userId(item.getUser().getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getIsAvailable())
                .build();

        if (item.getRequest() != null) {
            dto.setRequestId(item.getRequest().getId());
        }

        return dto;
    }

    public static List<ItemDto> listOf(List<Item> items) {
        return items.stream().map(ItemDto::of).collect(Collectors.toList());
    }
}
