package ru.practicum.shareit.request.repository;


import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    @Query(value = "select ir from ItemRequest as ir " +
            "where ir.user.id = ?1 " +
            "order by ir.created desc")
    List<ItemRequest> findAllByUserId(long userId);

    @Query(value = "select ir from ItemRequest as ir")
    List<ItemRequest> findRequests(Pageable pageable);

    @Query(value = "select ir from ItemRequest as ir")
    List<ItemRequest> findRequests(Sort sorting);
}
