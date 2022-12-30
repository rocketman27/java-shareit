package ru.practicum.shareit.item.mappers;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public class CommentMapper {
    public static CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                         .withId(comment.getId())
                         .withText(comment.getText())
                         .withAuthorName(comment.getAuthor().getName())
                         .withCreated(comment.getCreated())
                         .build();
    }

    public static Comment toComment(CommentDto commentDto, User author, Item item) {
        return Comment.builder()
                .withText(commentDto.getText())
                .withItem(item)
                .withAuthor(author)
                .build();
    }
}
