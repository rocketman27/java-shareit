package ru.practicum.shareit.item.repositories;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repositories.ItemRequestRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private TestEntityManager em;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Test
    void contextLoads() {
        Assertions.assertNotNull(em);
        Assertions.assertNotNull(userRepository);
        Assertions.assertNotNull(itemRepository);
        Assertions.assertNotNull(itemRequestRepository);
    }

    @Test
    void searchByNameTest() {
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

        List<Item> items = itemRepository.search("screwdriver");
        Assertions.assertEquals(1, items.size());
        Assertions.assertEquals(item, items.get(0));
    }

    @Test
    void searchByDescriptionTest() {
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

        List<Item> items = itemRepository.search("compact");
        Assertions.assertEquals(1, items.size());
        Assertions.assertEquals(item, items.get(0));
    }

    @Test
    void findByRequestIdTest() {
        User user = User.builder()
                        .withName("User")
                        .withEmail("user_email@gmail.com")
                        .build();

        user = userRepository.save(user);

        User requester = User.builder()
                             .withName("Requester")
                             .withEmail("requester_email@gmail.com")
                             .build();

        requester = userRepository.save(requester);

        ItemRequest request = ItemRequest.builder()
                                         .withDescription("Screwdriver")
                                         .withRequestor(requester)
                                         .build();

        request = itemRequestRepository.save(request);

        Item item = Item.builder()
                        .withName("Screwdriver")
                        .withDescription("Compact screwdriver")
                        .withOwner(user)
                        .withRequest(request)
                        .withAvailable(true)
                        .build();

        item = itemRepository.save(item);

        List<Item> items = itemRepository.findByRequestId(request.getId());
        Assertions.assertEquals(1, items.size());
        Assertions.assertEquals(item, items.get(0));
    }
}