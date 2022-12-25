package ru.practicum.shareit.item.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query
    List<Comment> findByItemId(long itemId);

    @Query
    List<Comment> findByAuthorIdAndItemId(long authorId, long itemId);

}
