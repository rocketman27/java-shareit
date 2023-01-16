package ru.practicum.shareit.request.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findByRequestorIdOrderByCreated(long requestorId);

    List<ItemRequest> findByRequestorIdNot(long requestorId, Pageable pageable);

    List<ItemRequest> findByRequestorIdNot(long requestorId);
}
