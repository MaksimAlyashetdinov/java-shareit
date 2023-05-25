package ru.practicum.shareit.request;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.User;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTest {

    @Autowired
    ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    ItemRequestServiceImpl itemRequestService;

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @Test
    void createTest() throws Exception {
        User user = createUser(1);
        ItemRequestDto itemRequestDto = createItemRequestDto(1, user);
        Mockito.when(itemRequestService.create(Mockito.any(), Mockito.anyLong(), Mockito.any()))
               .thenReturn(itemRequestDto);
        mvc.perform(post("/requests")
                   .header(USER_ID_HEADER, user.getId())
                   .content(mapper.writeValueAsString(itemRequestDto))
                   .characterEncoding(StandardCharsets.UTF_8)
                   .contentType(MediaType.APPLICATION_JSON)
                   .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(content().json(mapper.writeValueAsString(itemRequestDto)));
    }

    @Test
    void getByIdTest() throws Exception {
        User user = createUser(1);
        ItemRequestDto itemRequestDto = createItemRequestDto(1, user);
        Mockito.when(itemRequestService.getById(Mockito.anyLong(), Mockito.anyLong()))
               .thenReturn(itemRequestDto);
        mvc.perform(get("/requests" + "/1")
                   .characterEncoding(StandardCharsets.UTF_8)
                   .contentType(MediaType.APPLICATION_JSON)
                   .header(USER_ID_HEADER, 1L)
                   .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(content().json(mapper.writeValueAsString(itemRequestDto)));
    }

    @Test
    void getAllByUserTest() throws Exception {
        User user = createUser(1);
        ItemRequestDto itemRequestDto = createItemRequestDto(1, user);
        Mockito.when(itemRequestService.getAllByUser(user.getId())).thenReturn(List.of(itemRequestDto));
        mvc.perform(get("/requests")
                   .characterEncoding(StandardCharsets.UTF_8)
                   .contentType(MediaType.APPLICATION_JSON)
                   .header(USER_ID_HEADER, 1L)
                   .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(content().json(mapper.writeValueAsString(List.of(itemRequestDto))));
    }

    @Test
    void getAllTest() throws Exception {
        User user = createUser(1);
        ItemRequestDto itemRequestDto = createItemRequestDto(1, user);
        Mockito.when(itemRequestService.getAll(user.getId(), 0, 10))
               .thenReturn(List.of(itemRequestDto));
        mvc.perform(get("/requests/all")
                   .characterEncoding(StandardCharsets.UTF_8)
                   .contentType(MediaType.APPLICATION_JSON)
                   .header(USER_ID_HEADER, 1L)
                   .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(content().json(mapper.writeValueAsString(List.of(itemRequestDto))));
    }

    private ItemRequestDto createItemRequestDto(long id, User user) {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(id);
        itemRequestDto.setRequester(user);
        itemRequestDto.setDescription("Description for item request " + id);
        return itemRequestDto;
    }

    private User createUser(long id) {
        User user = new User();
        user.setId(id);
        user.setName("User " + id);
        user.setEmail("user_" + id + "@email.ru");
        return user;
    }
}