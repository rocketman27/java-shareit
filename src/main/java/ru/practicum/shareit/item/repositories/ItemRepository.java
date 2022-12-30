package ru.practicum.shareit.item.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("select i from Item i " +
            "where i.available = true " +
            "and upper(i.description) like upper(concat('%', ?1, '%')) " +
            "or (upper(i.name) like upper(concat('%', ?2, '%')))")
    List<Item> search(String text, long userId);
}
