package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemResponseDto {
    private Integer itemId;

    private String name;

    private String ownerId;
}
