package ru.practicum.shareit.item;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.utils.ItemMapper;
import ru.practicum.shareit.user.User;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {

    @Autowired
    ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    ItemService itemService;

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private User itemOwner;
    private Item item;
    private CommentDtoRequest commentDtoRequest;
    private User user;

    @BeforeEach
    void setUp() {
        itemOwner = createUser(1);
        item = createItem(1, itemOwner.getId());
        commentDtoRequest = createCommentDtoRequest(1);
        user = createUser(2);
    }

    @Test
    void createItemTest_Ok() throws Exception {
        Mockito.when(itemService.createItem(anyLong(), any()))
               .thenReturn(item);
        mvc.perform(post("/items")
                   .header(USER_ID_HEADER, 2L)
                   .content(mapper.writeValueAsString(item))
                   .characterEncoding(StandardCharsets.UTF_8)
                   .contentType(MediaType.APPLICATION_JSON)
                   .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(content().json(mapper.writeValueAsString(item)));
    }

    @Test
    void createItemTest_NotFoundException() throws Exception {
        Mockito.when(itemService.createItem(anyLong(), any()))
               .thenThrow(NotFoundException.class);
        mvc.perform(post("/items")
                   .header(USER_ID_HEADER, 2L)
                   .content(mapper.writeValueAsString(item))
                   .characterEncoding(StandardCharsets.UTF_8)
                   .contentType(MediaType.APPLICATION_JSON)
                   .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isNotFound());
    }

    @Test
    void createItemTest_ValidationException() throws Exception {
        Mockito.when(itemService.createItem(anyLong(), any()))
               .thenThrow(ValidationException.class);
        mvc.perform(post("/items")
                   .header(USER_ID_HEADER, 2L)
                   .content(mapper.writeValueAsString(item))
                   .characterEncoding(StandardCharsets.UTF_8)
                   .contentType(MediaType.APPLICATION_JSON)
                   .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isBadRequest());
    }

    @Test
    void getByIdTest_Ok() throws Exception {
        ItemDto itemDto = ItemMapper.mapToItemDto(item, List.of(new CommentDto()));
        Mockito.when(itemService.getById(itemOwner.getId(), item.getId()))
               .thenReturn(itemDto);
        mvc.perform(get("/items" + "/1")
                   .characterEncoding(StandardCharsets.UTF_8)
                   .contentType(MediaType.APPLICATION_JSON)
                   .header(USER_ID_HEADER, 1L)
                   .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(content().json(mapper.writeValueAsString(itemDto)));
    }

    @Test
    void getByIdTest_NotFoundException() throws Exception {
        Mockito.when(itemService.getById(itemOwner.getId(), item.getId()))
               .thenThrow(NotFoundException.class);
        mvc.perform(get("/items" + "/1")
                   .characterEncoding(StandardCharsets.UTF_8)
                   .contentType(MediaType.APPLICATION_JSON)
                   .header(USER_ID_HEADER, 1L)
                   .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isNotFound());
    }

    @Test
    void getByNameTest_Ok() throws Exception {
        Mockito.when(itemService.getByName(Mockito.any(), Mockito.anyInt(), Mockito.anyInt()))
               .thenReturn(List.of(item));
        mvc.perform(get("/items/search" + "?text=" + item.getName())
                   .characterEncoding(StandardCharsets.UTF_8)
                   .contentType(MediaType.APPLICATION_JSON)
                   .header(USER_ID_HEADER, 1L)
                   .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(content().json(mapper.writeValueAsString(List.of(item))));
    }

    @Test
    void getByNameTest_ValidationException() throws Exception {
        Mockito.when(itemService.getByName(Mockito.any(), Mockito.anyInt(), Mockito.anyInt()))
               .thenThrow(ValidationException.class);
        mvc.perform(get("/items/search" + "?text=" + item.getName())
                   .characterEncoding(StandardCharsets.UTF_8)
                   .contentType(MediaType.APPLICATION_JSON)
                   .header(USER_ID_HEADER, 1L)
                   .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isBadRequest());
    }

    @Test
    void getAllItemsByUserIdTest_Ok() throws Exception {
        ItemDto itemDto = ItemMapper.mapToItemDto(item, new ArrayList<>());
        Mockito.when(itemService.getAllItemsByUserId(itemOwner.getId(), 0, 10))
               .thenReturn(List.of(itemDto));
        mvc.perform(get("/items")
                   .characterEncoding(StandardCharsets.UTF_8)
                   .contentType(MediaType.APPLICATION_JSON)
                   .header(USER_ID_HEADER, 1L)
                   .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(content().json(mapper.writeValueAsString(List.of(itemDto))));
    }

    @Test
    void getAllItemsByUserIdTest_NotFoundException() throws Exception {
        Mockito.when(itemService.getAllItemsByUserId(itemOwner.getId(), 0, 10))
               .thenThrow(NotFoundException.class);
        mvc.perform(get("/items")
                   .characterEncoding(StandardCharsets.UTF_8)
                   .contentType(MediaType.APPLICATION_JSON)
                   .header(USER_ID_HEADER, 1L)
                   .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isNotFound());
    }

    @Test
    void getAllItemsByUserIdTest_ValidationException() throws Exception {
        Mockito.when(itemService.getAllItemsByUserId(itemOwner.getId(), 0, 10))
               .thenThrow(ValidationException.class);
        mvc.perform(get("/items")
                   .characterEncoding(StandardCharsets.UTF_8)
                   .contentType(MediaType.APPLICATION_JSON)
                   .header(USER_ID_HEADER, 1L)
                   .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isBadRequest());
    }

    @Test
    void updateItemTest_Ok() throws Exception {
        Mockito.when(itemService.updateItem(Mockito.anyLong(), Mockito.anyLong(), Mockito.any()))
               .thenReturn(item);
        mvc.perform(patch("/items/" + item.getId())
                   .content(mapper.writeValueAsString(item))
                   .characterEncoding(StandardCharsets.UTF_8)
                   .contentType(MediaType.APPLICATION_JSON)
                   .header(USER_ID_HEADER, 1L)
                   .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(content().json(mapper.writeValueAsString(item)));
    }

    @Test
    void updateItemTest_NotFoundException() throws Exception {
        Mockito.when(itemService.updateItem(Mockito.anyLong(), Mockito.anyLong(), Mockito.any()))
               .thenThrow(NotFoundException.class);
        mvc.perform(patch("/items/" + item.getId())
                   .content(mapper.writeValueAsString(item))
                   .characterEncoding(StandardCharsets.UTF_8)
                   .contentType(MediaType.APPLICATION_JSON)
                   .header(USER_ID_HEADER, 1L)
                   .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isNotFound());
    }

    @Test
    void deleteItemTest_Ok() throws Exception {
        mvc.perform(delete("/items/" + item.getId())
                   .characterEncoding(StandardCharsets.UTF_8)
                   .contentType(MediaType.APPLICATION_JSON)
                   .header(USER_ID_HEADER, itemOwner.getId())
                   .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk());
    }

    @Test
    void deleteItemTest_NotFoundException() throws Exception {
        Mockito.when(itemService.deleteItem(anyLong()))
               .thenThrow(NotFoundException.class);
        mvc.perform(delete("/items/" + item.getId())
                   .characterEncoding(StandardCharsets.UTF_8)
                   .contentType(MediaType.APPLICATION_JSON)
                   .header(USER_ID_HEADER, itemOwner.getId())
                   .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isNotFound());
    }

    @Test
    void addCommentToItemTest_Ok() throws Exception {
        CommentDto comment = createCommentDto(1, user);
        Mockito.when(itemService.addCommentToItem(Mockito.anyLong(), Mockito.anyLong(), Mockito.any()))
               .thenReturn(comment);
        mvc.perform(post("/items/" + item.getId() + "/comment")
                   .header(USER_ID_HEADER, user.getId())
                   .content(mapper.writeValueAsString(commentDtoRequest))
                   .characterEncoding(StandardCharsets.UTF_8)
                   .contentType(MediaType.APPLICATION_JSON)
                   .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(content().json(mapper.writeValueAsString(comment)));
    }

    @Test
    void addCommentToItemTest_NotFoundException() throws Exception {
        Mockito.when(itemService.addCommentToItem(Mockito.anyLong(), Mockito.anyLong(), Mockito.any()))
               .thenThrow(NotFoundException.class);
        mvc.perform(post("/items/" + item.getId() + "/comment")
                   .header(USER_ID_HEADER, user.getId())
                   .content(mapper.writeValueAsString(commentDtoRequest))
                   .characterEncoding(StandardCharsets.UTF_8)
                   .contentType(MediaType.APPLICATION_JSON)
                   .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isNotFound());
    }

    @Test
    void addCommentToItemTest_ValidationException() throws Exception {
        Mockito.when(itemService.addCommentToItem(Mockito.anyLong(), Mockito.anyLong(), Mockito.any()))
               .thenThrow(ValidationException.class);
        mvc.perform(post("/items/" + item.getId() + "/comment")
                   .header(USER_ID_HEADER, user.getId())
                   .content(mapper.writeValueAsString(commentDtoRequest))
                   .characterEncoding(StandardCharsets.UTF_8)
                   .contentType(MediaType.APPLICATION_JSON)
                   .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isBadRequest());
    }

    private User createUser(long id) {
        User user = new User();
        user.setId(id);
        user.setName("User " + id);
        user.setEmail("user_" + id + "@email.ru");
        return user;
    }

    private Item createItem(long id, long ownerId) {
        Item item = new Item();
        item.setId(id);
        item.setName("Item " + id);
        item.setDescription("Description for item " + id);
        item.setOwnerId(ownerId);
        item.setAvailable(true);
        return item;
    }

    private CommentDto createCommentDto(long id, User user) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(id);
        commentDto.setAuthorName(user.getName());
        commentDto.setText("Text for comment " + id);
        return commentDto;
    }

    private CommentDtoRequest createCommentDtoRequest(long id) {
        CommentDtoRequest commentDtoRequest = new CommentDtoRequest();
        commentDtoRequest.setId(id);
        commentDtoRequest.setText("Text for comment " + id);
        return commentDtoRequest;
    }
}