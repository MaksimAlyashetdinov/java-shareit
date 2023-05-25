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
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
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

    @Test
    void createItemTest() throws Exception {
        User itemOwner = createUser(1);
        Item item = createItem(1, itemOwner.getId());
        Mockito.when(itemService.createItem(anyLong(), any())).thenReturn(item);
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
    void getByIdTest() throws Exception {
        User itemOwner = createUser(1);
        Item item = createItem(1, itemOwner.getId());
        ItemDto itemDto = ItemMapper.mapToItemDto(item, List.of(new CommentDto()));
        Mockito.when(itemService.getById(itemOwner.getId(), item.getId())).thenReturn(itemDto);
        mvc.perform(get("/items" + "/1")
                   .characterEncoding(StandardCharsets.UTF_8)
                   .contentType(MediaType.APPLICATION_JSON)
                   .header(USER_ID_HEADER, 1L)
                   .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(content().json(mapper.writeValueAsString(itemDto)));
    }

    @Test
    void getByNameTest() throws Exception {
        User itemOwner = createUser(1);
        Item item = createItem(1, itemOwner.getId());
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
    void getAllItemsByUserIdTest() throws Exception {
        User itemOwner = createUser(1);
        Item item = createItem(1, itemOwner.getId());
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
    void updateItemTest() throws Exception {
        User itemOwner = createUser(1);
        Item item = createItem(1, itemOwner.getId());
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
    void deleteItemTest() throws Exception {
        User itemOwner = createUser(1);
        Item item = createItem(1, itemOwner.getId());
        mvc.perform(delete("/items/" + item.getId())
                   .characterEncoding(StandardCharsets.UTF_8)
                   .contentType(MediaType.APPLICATION_JSON)
                   .header(USER_ID_HEADER, itemOwner.getId())
                   .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk());
    }

    @Test
    void addCommentToItemTest() throws Exception {
        User itemOwner = createUser(1);
        User user = createUser(2);
        Item item = createItem(1, itemOwner.getId());
        CommentDto comment = createCommentDto(1, user);
        CommentDtoRequest commentDtoRequest = createCommentDtoRequest(1);
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