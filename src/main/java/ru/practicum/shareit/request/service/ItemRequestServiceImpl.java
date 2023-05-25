package ru.practicum.shareit.request.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.item.utils.ItemMapper;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserRepository;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ItemRequestServiceImpl implements ItemRequestService {

    private final Sort SORT = Sort.by(Sort.Direction.DESC, "created");
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDto create(ItemRequestDto requestDto, Long userId, LocalDateTime createDate) {
        User requester = userRepository.findById(userId)
                                       .orElseThrow(() -> new NotFoundException(
                                               "User with id = " + userId + " not exist."));
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(requestDto);
        validateItemRequest(itemRequest);
        itemRequest.setRequester(requester);
        itemRequest.setCreated(createDate);
        itemRequestRepository.save(itemRequest);
        log.info("Item request successfully added: " + itemRequest);
        return ItemRequestMapper.toItemRequestDto(itemRequest);
    }

    @Override
    public ItemRequestDto getById(Long requestId, Long userId) {
        containsUser(userId);
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                                                       .orElseThrow(() -> new NotFoundException(
                                                               "Item request with id = " + requestId
                                                                       + " not exist."));
        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
        itemRequestDto.setItems(getItemsForItemRequest(requestId));
        log.info("Get item request " + itemRequest);
        return itemRequestDto;
    }

    @Override
    public List<ItemRequestDto> getAllByUser(Long userId) {
        containsUser(userId);
        List<ItemRequestDto> userItemRequestsDto = new ArrayList<>();
        List<ItemRequest> userItemRequests = itemRequestRepository.findByRequesterId(userId, SORT);
        if (!userItemRequests.isEmpty()) {
            userItemRequestsDto = userItemRequests.stream()
                                                  .map(itemRequest -> ItemRequestMapper.toItemRequestDto(
                                                          itemRequest))
                                                  .collect(Collectors.toList());
            userItemRequestsDto.stream()
                               .forEach(itemRequestDto -> itemRequestDto.setItems(
                                       getItemsForItemRequest(itemRequestDto.getId())));
        }
        log.info("Get item requests for user with id = " + userId);
        return userItemRequestsDto;
    }

    @Override
    public List<ItemRequestDto> getAll(Long userId, Integer from, Integer size) {
        containsUser(userId);
        validatePage(from, size);
        PageRequest pageRequest = PageRequest.of(from / size, size, SORT);
        List<ItemRequest> allItemRequests = itemRequestRepository.findAllByRequesterIdNot(userId,
                pageRequest);
        List<ItemRequestDto> allItemRequestsDto = allItemRequests.stream()
                                                                 .map(itemRequest -> ItemRequestMapper.toItemRequestDto(
                                                                         itemRequest))
                                                                 .collect(Collectors.toList());
        allItemRequestsDto.forEach(itemRequestDto -> itemRequestDto.setItems(getItemsForItemRequest(
                itemRequestDto.getId())));
        log.info("Get all item requests from user with id = " + userId);
        return allItemRequestsDto;
    }

    private void containsUser(long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("User not found.");
        }
    }

    private void validateItemRequest(ItemRequest itemRequest) {
        if (itemRequest.getDescription() == null || itemRequest.getDescription().isBlank()) {
            throw new ValidationException("Description can't be empty.");
        }
    }

    private void validatePage(Integer from, Integer size) {
        if (from < 0) {
            throw new ValidationException(
                    "It is not possible to start the display with a negative element.");
        }
        if (size < 1) {
            throw new ValidationException("The number of records cannot be less than 1.");
        }
    }

    private List<ItemDto> getItemsForItemRequest(long itemRequestId) {
        List<ItemDto> itemsForItemRequest = new ArrayList<>();
        List<Item> items = itemRepository.findByRequestId(itemRequestId);
        if (!items.isEmpty()) {
            itemsForItemRequest = items.stream()
                                       .map(item -> ItemMapper.mapToItemDto(item, null))
                                       .collect(Collectors.toList());
        }
        return itemsForItemRequest;
    }
}