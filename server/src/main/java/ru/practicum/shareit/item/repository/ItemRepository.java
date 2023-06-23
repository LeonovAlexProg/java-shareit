package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ItemRepository extends CrudRepository<Item, Long> {
    Optional<List<Item>> findItemsByUserId(Long userId, Sort sort);

    @Query(value = "select i from Item as i " +
            "where (lower(i.name) like lower(concat('%', ?1, '%')) or " +
            "lower(i.description) like lower(concat('%', ?1, '%'))) and " +
            "i.isAvailable = true")
    Optional<List<Item>> findItemsLike(String text);

    @Query(value = "select case when count(b) > 0 then true else false END " +
            "from Booking as b " +
            "join b.user as u " +
            "join b.item as i " +
            "where i.id = ?1 and " +
            "u.id = ?2 and " +
            "b.end < ?3")
    Boolean itemWasRentedByUser(long itemId, long userId, LocalDateTime localDateTime);
}
