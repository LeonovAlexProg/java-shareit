package ru.practicum.shareit.request.dto;


import lombok.Builder;
import lombok.Data;
import org.springframework.lang.Nullable;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ItemRequestDto {
    @Null
    private Long id;

    @NotBlank
    private String description;

    @Null
    private LocalDateTime created;

    @Null
    private List<ItemDto> items;
}
