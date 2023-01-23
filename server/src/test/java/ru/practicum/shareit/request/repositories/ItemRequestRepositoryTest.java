package ru.practicum.shareit.request.repositories;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@DataJpaTest
class ItemRequestRepositoryTest {
    @Autowired
    private TestEntityManager em;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Test
    void contextLoads() {
        Assertions.assertNotNull(em);
        Assertions.assertNotNull(userRepository);
        Assertions.assertNotNull(itemRequestRepository);
    }

    @Test
    void findByRequesterIdOrderByCreatedTest() {
        User requester = User.builder()
                             .withName("User")
                             .withEmail("user_email@gmail.com")
                             .build();

        requester = userRepository.save(requester);

        ItemRequest itemRequest = ItemRequest.builder()
                                             .withDescription("Item Request")
                                             .withRequestor(requester)
                                             .build();

        itemRequest = itemRequestRepository.save(itemRequest);

        List<ItemRequest> requests = itemRequestRepository.findByRequestorIdOrderByCreated(requester.getId());
        Assertions.assertEquals(1, requests.size());
        Assertions.assertEquals(itemRequest, requests.get(0));
    }

    @Test
    void findByRequesterIdNotTest() {
        User requester = User.builder()
                             .withName("Requester")
                             .withEmail("requester_email@gmail.com")
                             .build();

        User requester1 = User.builder()
                             .withName("Requester 1")
                             .withEmail("requester1_email@gmail.com")
                             .build();

        requester = userRepository.save(requester);
        requester1 = userRepository.save(requester1);

        ItemRequest itemRequest = ItemRequest.builder()
                                             .withDescription("Item Request")
                                             .withRequestor(requester)
                                             .build();

        ItemRequest itemRequest1 = ItemRequest.builder()
                                             .withDescription("Item Request 1")
                                             .withRequestor(requester1)
                                             .build();

        itemRequest = itemRequestRepository.save(itemRequest);
        itemRequest1 = itemRequestRepository.save(itemRequest1);


        List<ItemRequest> requests = itemRequestRepository.findByRequestorIdNot(requester1.getId());
        Assertions.assertEquals(1, requests.size());
        Assertions.assertEquals(itemRequest, requests.get(0));
    }
}
