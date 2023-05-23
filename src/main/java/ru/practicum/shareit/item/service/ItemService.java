package ru.practicum.shareit.item.service;

import java.util.List;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

public interface ItemService {

    Item createItem(long userId, Item item);

    ItemDto getById(long userId, long itemId);

    List<Item> getByName(String title, int from, int size);

    List<ItemDto> getAllItemsByUserId(long userId, int from, int size);

    Item updateItem(long userId, long itemId, Item item);

    void deleteItem(long itemId);

    CommentDto addCommentToItem(long userId, long itemId, CommentDtoRequest comment);
}