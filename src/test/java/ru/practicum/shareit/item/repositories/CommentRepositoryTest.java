package ru.practicum.shareit.item.repositories;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@DataJpaTest
class CommentRepositoryTest {
    @Autowired
    private TestEntityManager em;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;

    @Test
    void contextLoads() {
        Assertions.assertNotNull(em);
        Assertions.assertNotNull(commentRepository);
        Assertions.assertNotNull(userRepository);
        Assertions.assertNotNull(itemRepository);
    }

    @Test
    void findByItemIdTest() {
        User user = User.builder()
                        .withName("User")
                        .withEmail("user_email@gmail.com")
                        .build();

        user = userRepository.save(user);

        Item item = Item.builder()
                        .withName("Screwdriver")
                        .withDescription("Compact screwdriver")
                        .withOwner(user)
                        .withAvailable(true)
                        .build();

        item = itemRepository.save(item);

        Comment comment = Comment.builder()
                                 .withText("Comment")
                                 .withItem(item)
                                 .withAuthor(user)
                                 .build();

        comment = commentRepository.save(comment);

        List<Comment> comments = commentRepository.findByItemId(item.getId());
        Assertions.assertEquals(1, comments.size());
        Assertions.assertEquals(comment, comments.get(0));
    }

    @Test
    void findByAuthorIdAndItemIdTest() {
        User user = User.builder()
                        .withName("User")
                        .withEmail("user_email@gmail.com")
                        .build();

        User user2 = User.builder()
                        .withName("User 2")
                        .withEmail("user2_email@gmail.com")
                        .build();

        user = userRepository.save(user);
        user2 = userRepository.save(user2);

        Item item = Item.builder()
                        .withName("Screwdriver")
                        .withDescription("Compact screwdriver")
                        .withOwner(user)
                        .withAvailable(true)
                        .build();

        item = itemRepository.save(item);

        Comment comment = Comment.builder()
                                 .withText("Comment")
                                 .withItem(item)
                                 .withAuthor(user)
                                 .build();

        Comment comment2 = Comment.builder()
                                 .withText("Comment 2")
                                 .withItem(item)
                                 .withAuthor(user2)
                                 .build();

        comment = commentRepository.save(comment);
        comment2 = commentRepository.save(comment2);

        List<Comment> comments = commentRepository.findByAuthorIdAndItemId(user2.getId(), item.getId());
        Assertions.assertEquals(1, comments.size());
        Assertions.assertEquals(comment2, comments.get(0));
    }
}