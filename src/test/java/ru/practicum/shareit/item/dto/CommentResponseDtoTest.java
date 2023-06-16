package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

class CommentResponseDtoTest {
    LocalDateTime localDateTime = LocalDateTime.now();

    User user = User.builder()
            .id(1L)
            .name("zima")
            .email("zimablue@mail.ru")
            .build();

    Item item = Item.builder()
            .id(1L)
            .user(user)
            .name("drill")
            .description("drilling drill")
            .isAvailable(true)
            .build();

    Comment comment = Comment.builder()
            .id(1L)
            .user(user)
            .text("demo text")
            .created(localDateTime)
            .item(item)
            .build();

    @Test
    void of() {
        CommentResponseDto expectedDto;
        CommentResponseDto actualDto;

        expectedDto = CommentResponseDto.builder()
                .id(comment.getId())
                .authorName(comment.getUser().getName())
                .created(localDateTime)
                .text(comment.getText())
                .build();

        actualDto = CommentResponseDto.of(comment);

        Assertions.assertEquals(expectedDto, actualDto);
    }

    @Test
    void listOf() {
        List<CommentResponseDto> expectedList;
        List<CommentResponseDto> actualList;

        CommentResponseDto correctDto = CommentResponseDto.builder()
                .id(comment.getId())
                .authorName(comment.getUser().getName())
                .created(localDateTime)
                .text(comment.getText())
                .build();

        expectedList = List.of(correctDto, correctDto, correctDto);
        actualList = CommentResponseDto.listOf(List.of(comment, comment, comment));

        Assertions.assertEquals(expectedList, actualList);
    }
}