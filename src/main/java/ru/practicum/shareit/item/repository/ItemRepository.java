package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends CrudRepository<Item, Long> {
    Optional<List<Item>> findItemsByUserId(Long userId);

    @Query(value = "select i from Item as i " +
            "where (lower(i.name) like lower(concat('%', ?1, '%')) or " +
            "lower(i.description) like lower(concat('%', ?1, '%'))) and " +
            "i.isAvailable = true")
    Optional<List<Item>> findItemsLike(String text);
}
