package ru.practicum.shareit.item;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoRequest;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.utils.CommentMapper;
import ru.practicum.shareit.user.User;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
public class CommentMapperTest {

    @Test
    void toCommentDtoTest() {
        User user = createUser(1);
        Item item = createItem(1, 2);
        Comment comment = createComment(1, user, item);
        CommentDto commentDto = CommentMapper.toCommentDto(comment);
        assertEquals(comment.getId(), commentDto.getId());
        assertEquals(comment.getText(), commentDto.getText());
    }

    @Test
    void toCommentTest() {
        User user = createUser(1);
        Item item = createItem(1, 2);
        CommentDtoRequest commentDtoRequest = createCommentDtoRequest(1);
        Comment comment = CommentMapper.toComment(user, item, commentDtoRequest,LocalDateTime.now());
        assertEquals(comment.getId(), commentDtoRequest.getId());
        assertEquals(comment.getText(), commentDtoRequest.getText());
    }

    private User createUser(long id) {
        User user = new User();
        user.setId(id);
        user.setName("User " + id);
        user.setEmail("user_" + id + "@email.ru");
        return user;
    }

    private Item createItem(long id, long ownerId) {
        Item item = new Item();
        item.setId(id);
        item.setName("Item " + id);
        item.setDescription("Description for item " + id);
        item.setOwnerId(ownerId);
        item.setAvailable(true);
        return item;
    }

    private Comment createComment(long id, User user, Item item) {
        Comment comment = new Comment();
        comment.setId(id);
        comment.setText("Text for comment " + id);
        comment.setAuthor(user);
        comment.setItem(item);
        return comment;
    }

    private CommentDtoRequest createCommentDtoRequest(long id) {
        CommentDtoRequest commentDtoRequest = new CommentDtoRequest();
        commentDtoRequest.setId(id);
        commentDtoRequest.setText("Text for comment " + id);
        return commentDtoRequest;
    }
}
