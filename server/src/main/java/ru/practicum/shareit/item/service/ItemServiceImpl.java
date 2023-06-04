package ru.practicum.shareit.item.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.booking.utils.BookingMapper;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.item.utils.CommentMapper;
import ru.practicum.shareit.item.utils.ItemMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserRepository;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ItemServiceImpl implements ItemService {

    private final Sort sort = Sort.by(Direction.ASC, "id");
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemMapper itemMapper;
    private final BookingMapper bookingMapper;
    private final CommentMapper commentMapper;

    @Override
    public Item createItem(long userId, Item item) {
        containsUser(userId);
        item.setOwnerId(userId);
        log.info("Item successfully added: " + item);
        return itemRepository.save(item);
    }

    @Override
    public ItemDto getById(long userId, long itemId) {
        Item item = itemRepository.findById(itemId)
                                  .orElseThrow(() -> new NotFoundException("Item not found."));
        List<Comment> comments = commentRepository.findAllByItemId(item.getId());
        List<CommentDto> commentsDto = null;
        if (comments.size() > 0) {
            commentsDto = comments.stream()
                                  .map(comment -> commentMapper.toCommentDto(comment))
                                  .collect(Collectors.toList());
        }
        if (userId == item.getOwnerId()) {
            Booking lastBooking = bookingRepository.findFirstByItemIdAndStartIsBeforeAndStatusIsOrderByStartDesc(itemId, LocalDateTime.now(), BookingState.APPROVED);
            BookingDto lastBookingDto = null;
            if (lastBooking != null) {
                lastBookingDto = bookingMapper.mapToBookingDto(lastBooking);
            }
            Booking nextBooking = bookingRepository.findFirstByItemIdAndStartIsAfterAndStatusIsOrderByStartAsc(
                    itemId, LocalDateTime.now(), BookingState.APPROVED);
            BookingDto nextBookingDto = null;
            if (nextBooking != null) {
                nextBookingDto = bookingMapper.mapToBookingDto(nextBooking);
            }
            ItemDto itemWithBooking = itemMapper.mapToItemDtoWithBookings(item, lastBookingDto,
                    nextBookingDto, commentsDto);
            log.info("Get item with bookings: " + itemWithBooking);
            return itemWithBooking;
        }
        log.info("Get item: " + item);
        return itemMapper.mapToItemDto(item, commentsDto);
    }

    @Override
    public List<Item> getByName(String name, long userId, int from, int size) {
        containsUser(userId);
        if (name.isBlank()) {
            log.info("Get list with empty items name.");
            return new ArrayList<>();
        }
        PageRequest pageRequest = PageRequest.of(from / size, size, sort);
        List<Item> items = itemRepository.findAllByName(name, pageRequest);
        log.info("Get items by name: " + items);
        return items;
    }

    @Override
    public List<ItemDto> getAllItemsByUserId(long userId, int from, int size) {
        containsUser(userId);
        PageRequest pageRequest = PageRequest.of(from / size, size, sort);
        List<Item> items = itemRepository.findAllByOwnerId(userId, pageRequest);
        List<ItemDto> itemsDto = new ArrayList<>();
        if (!items.isEmpty()) {
            itemsDto = items.stream()
                 .map(i -> getById(userId, i.getId()))
                 .collect(Collectors.toList());
        }
        log.info("Get items by user id: " + itemsDto);
        return itemsDto;
    }

    @Override
    public Item updateItem(long userId, long itemId, Item item) {
        containsUser(userId);
        Item itemFromRepository = itemRepository.findById(itemId)
                                                .orElseThrow(() -> new NotFoundException(
                                                        "Item not found."));
        if (itemFromRepository.getOwnerId() != userId) {
            throw new NotFoundException(
                    "This item can update only user with id = " + itemFromRepository.getOwnerId());
        }
        if (item.getName() != null && !item.getName().isBlank()) {
            itemFromRepository.setName(item.getName());
        }
        if (item.getDescription() != null && !item.getDescription().isBlank()) {
            itemFromRepository.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            itemFromRepository.setAvailable(item.getAvailable());
        }
        log.info("Item updated: " + itemFromRepository);
        return itemRepository.save(itemFromRepository);
    }

    @Override
    public Item deleteItem(long itemId) {
        Item item = itemRepository.findById(itemId)
                                  .orElseThrow(() -> new NotFoundException("Item not found."));
        log.info("Deleted item with id: {}", itemId);
        itemRepository.delete(item);
        return item;
    }

    @Override
    public CommentDto addCommentToItem(long userId, long itemId,
            CommentDtoRequest commentDtoRequest) {
        User user = userRepository.findById(userId)
                                  .orElseThrow(() -> new NotFoundException("User not found."));
        Item item = itemRepository.findById(itemId)
                                  .orElseThrow(() -> new NotFoundException("Item not found."));
        if (commentDtoRequest == null || commentDtoRequest.getText()
                                                          .isBlank()) {
            throw new ValidationException("Text of comment can't be empty.");
        }
        if (bookingRepository.findFirstByItemIdAndBookerIdAndStatusAndEndIsBefore(itemId, userId,
                                     BookingState.APPROVED, LocalDateTime.now())
                             .isEmpty()) {
            throw new ValidationException("This user can't add comment to this item.");
        }
        Comment comment = commentMapper.toComment(user, item, commentDtoRequest,
                LocalDateTime.now());
        log.info("Add comment: " + comment);
        return commentMapper.toCommentDto(commentRepository.save(comment));
    }

    private void containsUser(long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("User with id = " + id + " not exist.");
        }
    }
}