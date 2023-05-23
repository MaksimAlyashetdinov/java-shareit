package ru.practicum.shareit.request.storage;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.ItemRequest;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findByRequesterId(long requesterId, Sort sort);

    Page<ItemRequest> findAllByRequesterIdNot(long userId, Pageable pageable);
}