package ru.practicum.shareit.item.utils;

import java.time.LocalDateTime;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoRequest;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

@Component
public class CommentMapper {

    public static CommentDto toCommentDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setText(comment.getText());
        commentDto.setAuthorName(comment.getAuthor().getName());
        commentDto.setCreated(comment.getCreated());
        return commentDto;
    }

    public static Comment toComment(User author, Item item, CommentDtoRequest commentDtoRequest,
            LocalDateTime now) {
        Comment comment = new Comment();
        comment.setId(commentDtoRequest.getId());
        comment.setText(commentDtoRequest.getText());
        comment.setAuthor(author);
        comment.setItem(item);
        comment.setCreated(now);
        return comment;
    }
}