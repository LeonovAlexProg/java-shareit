package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Builder
@Data
public class CommentResponseDto {
    @NotNull
    private Long id;
    @NotNull
    private String text;
    @NotNull
    private String authorName;
    @NotNull
    private LocalDateTime created;
}
