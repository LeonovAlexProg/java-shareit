package ru.practicum.shareit.request.dto;


import lombok.Builder;
import lombok.Data;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ItemRequestDto {
    @NotBlank
    private String description;

    @Null
    private LocalDateTime creation;

    @Null
    List<ItemResponseDto> responses;
}
