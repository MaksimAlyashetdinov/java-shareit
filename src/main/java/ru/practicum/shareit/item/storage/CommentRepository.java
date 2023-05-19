package ru.practicum.shareit.item.storage;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByItemId(long itemId);
}