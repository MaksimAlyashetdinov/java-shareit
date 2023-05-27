package ru.practicum.shareit.item;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoRequest;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.utils.CommentMapper;
import ru.practicum.shareit.user.User;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
public class CommentMapperTest {

    private User user;
    private Item item;
    private Comment comment;
    private CommentDtoRequest commentDtoRequest;

    @BeforeEach
    void setUp() {
        user = createUser(1);
        item = createItem(1, 2);
        comment = createComment(1, user, item);
        commentDtoRequest = createCommentDtoRequest(1);
    }

    @Test
    void toCommentDtoTest_Ok() {
        CommentDto commentDto = CommentMapper.toCommentDto(comment);
        assertThat(comment.getId()).isEqualTo(commentDto.getId());
        assertThat(comment.getText()).isEqualTo(commentDto.getText());
    }

    @Test
    void toCommentDtoTest_textIsNull() {
        comment.setText(null);
        ValidationException exception = assertThrows(ValidationException.class,
                () -> CommentMapper.toCommentDto(comment));
        assertThat("All comment fields must be filled in.").isEqualTo(exception.getMessage());
    }

    @Test
    void toCommentDtoTest_createdIsNull() {
        comment.setCreated(null);
        ValidationException exception = assertThrows(ValidationException.class,
                () -> CommentMapper.toCommentDto(comment));
        assertThat("All comment fields must be filled in.").isEqualTo(exception.getMessage());
    }

    @Test
    void toCommentDtoTest_authorIsNull() {
        comment.setAuthor(null);
        ValidationException exception = assertThrows(ValidationException.class,
                () -> CommentMapper.toCommentDto(comment));
        assertThat("All comment fields must be filled in.").isEqualTo(exception.getMessage());
    }

    @Test
    void toCommentDtoTest_ItemIsNull() {
        comment.setItem(null);
        ValidationException exception = assertThrows(ValidationException.class,
                () -> CommentMapper.toCommentDto(comment));
        assertThat("All comment fields must be filled in.").isEqualTo(exception.getMessage());
    }

    @Test
    void toCommentTest_Ok() {
        Comment comment = CommentMapper.toComment(user, item, commentDtoRequest,
                LocalDateTime.now());
        assertThat(comment.getId()).isEqualTo(commentDtoRequest.getId());
        assertThat(comment.getText()).isEqualTo(commentDtoRequest.getText());
    }

    @Test
    void toCommentTest_authorIsNull() {
        ValidationException exception = assertThrows(ValidationException.class,
                () -> CommentMapper.toComment(null, item, commentDtoRequest,
                        LocalDateTime.of(2023, 12, 1, 8, 0)));
        assertThat("Author can't be empty.").isEqualTo(exception.getMessage());
    }

    @Test
    void toCommentTest_itemIsNull() {
        ValidationException exception = assertThrows(ValidationException.class,
                () -> CommentMapper.toComment(user, null, commentDtoRequest,
                        LocalDateTime.of(2023, 12, 1, 8, 0)));
        assertThat("Item can't be empty.").isEqualTo(exception.getMessage());
    }

    @Test
    void toCommentTest_textIsNull() {
        commentDtoRequest.setText(null);
        ValidationException exception = assertThrows(ValidationException.class,
                () -> CommentMapper.toComment(user, item, commentDtoRequest,
                        LocalDateTime.of(2023, 12, 1, 8, 0)));
        assertThat("Comment text can't be empty.").isEqualTo(exception.getMessage());
    }

    @Test
    void toCommentTest_createdIsNull() {
        ValidationException exception = assertThrows(ValidationException.class,
                () -> CommentMapper.toComment(user, item, commentDtoRequest, null));
        assertThat("Date of create can't be empty.").isEqualTo(exception.getMessage());
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
        comment.setCreated(LocalDateTime.of(2023, 12, 1, 8, 0));
        return comment;
    }

    private CommentDtoRequest createCommentDtoRequest(long id) {
        CommentDtoRequest commentDtoRequest = new CommentDtoRequest();
        commentDtoRequest.setId(id);
        commentDtoRequest.setText("Text for comment " + id);
        return commentDtoRequest;
    }
}