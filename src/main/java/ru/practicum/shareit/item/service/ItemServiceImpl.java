package ru.practicum.shareit.item.service;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemDbStorage;
import ru.practicum.shareit.user.storage.UserDbStorage;

@Service
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemDbStorage itemDbStorage;
    private final UserDbStorage userDbStorage;

    public ItemServiceImpl(ItemDbStorage itemDbStorage, UserDbStorage userDbStorage) {
        this.itemDbStorage = itemDbStorage;
        this.userDbStorage = userDbStorage;
    }

    @Override
    public Item createItem(Long userId, Item item) {
        containsUser(userId);
        validateItem(item);
        item.setOwnerId(userId);
        log.info("Item successfully added: " + item);
        return itemDbStorage.create(item);
    }

    @Override
    public Item getById(Long itemId) {
        containsItem(itemId);
        log.info("Requested item with ID = " + itemId);
        return itemDbStorage.getById(itemId);
    }

    @Override
    public List<Item> getByName(String name) {
        if (name.isBlank()) {
            log.info("Get list with empty items name.");
            return new ArrayList<>();
        }
        log.info("List of all items with name {}: " + itemDbStorage.getAllByName(name).size(),
                name);
        return itemDbStorage.getAllByName(name);
    }

    @Override
    public List<Item> getAllItemsByUserId(Long userId) {
        containsUser(userId);
        log.info("List of all items for user with id {}: " + itemDbStorage.getAllItemsByUserId(
                userId).size(), userId);
        return itemDbStorage.getAllItemsByUserId(userId);
    }

    @Override
    public Item updateItem(Long userId, Long itemId, Item item) {
        containsUser(userId);
        containsItem(itemId);
        validateUpdateItem(userId, itemId);
        log.info("Item successfully updated: " + item);
        return itemDbStorage.update(itemId, item);
    }

    @Override
    public void deleteItem(Long itemId) {
        containsItem(itemId);
        log.info("Deleted item with id: {}", itemId);
        itemDbStorage.delete(itemId);
    }

    private void containsItem(Long id) {
        if (!itemDbStorage.containsItem(id)) {
            throw new NotFoundException("Item with id = " + id + " not exist.");
        }
    }

    private void containsUser(Long id) {
        if (!userDbStorage.containsUser(id)) {
            throw new NotFoundException("User with id = " + id + " not exist.");
        }
    }

    private void validateItem(Item item) {
        if (item.getName() == null || item.getName().isBlank()) {
            throw new ValidationException("You must specify the name.");
        }
        if (item.getDescription() == null || item.getDescription().isBlank()) {
            throw new ValidationException("You must specify the description.");
        }
        if (item.getAvailable() == null) {
            throw new ValidationException("You must specify the available.");
        }
    }

    private void validateUpdateItem(long userId, long itemId) {
        Item itemFromMemory = itemDbStorage.getById(itemId);
        if (itemFromMemory.getOwnerId() != userId) {
            throw new NotFoundException(
                    "This item can update only user with id = " + itemFromMemory.getOwnerId());
        }
    }
}