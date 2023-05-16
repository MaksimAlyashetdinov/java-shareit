package ru.practicum.shareit.item.storage;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

public interface ItemStorage extends JpaRepository<Item, Long> {

    @Query(value = "select * from items i where (lower(i.name) like lower(concat('%', :name,'%')) or lower(i.description) like lower(concat('%', :name,'%'))) and i.available = true", nativeQuery = true)
    List<Item> findAllByName(String name);

    List<Item> findAllByOwnerId(Long ownerId);
}