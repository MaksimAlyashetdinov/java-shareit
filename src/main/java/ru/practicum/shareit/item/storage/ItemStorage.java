package ru.practicum.shareit.item.storage;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

public interface ItemStorage extends JpaRepository<Item, Long> {

    @Query("select i from items as i join fetch commons as")
    List<Item> findAllByName(String name);

    List<Item> findAllByOwnerId(Long ownerId);
}
