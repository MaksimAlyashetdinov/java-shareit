package ru.practicum.shareit.request;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
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
    private User user;
    private ItemRequestDto itemRequestDto;

    @BeforeEach
    void setUp() {
        user = createUser(1);
        itemRequestDto = createItemRequestDto(1, user);
    }

    @Test
    void createTest_Ok() throws Exception {
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
    void createTest_NotFoundException() throws Exception {
        Mockito.when(itemRequestService.create(Mockito.any(), Mockito.anyLong(), Mockito.any()))
               .thenThrow(NotFoundException.class);
        mvc.perform(post("/requests")
                   .header(USER_ID_HEADER, user.getId())
                   .content(mapper.writeValueAsString(itemRequestDto))
                   .characterEncoding(StandardCharsets.UTF_8)
                   .contentType(MediaType.APPLICATION_JSON)
                   .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isNotFound());
    }

    @Test
    void getByIdTest_Ok() throws Exception {
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
    void getByIdTest_NotFoundException() throws Exception {
        Mockito.when(itemRequestService.getById(Mockito.anyLong(), Mockito.anyLong()))
               .thenThrow(NotFoundException.class);
        mvc.perform(get("/requests" + "/1")
                   .characterEncoding(StandardCharsets.UTF_8)
                   .contentType(MediaType.APPLICATION_JSON)
                   .header(USER_ID_HEADER, 1L)
                   .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isNotFound());
    }

    @Test
    void getAllByUserTest_Ok() throws Exception {
        Mockito.when(itemRequestService.getAllByUser(user.getId()))
               .thenReturn(List.of(itemRequestDto));
        mvc.perform(get("/requests")
                   .characterEncoding(StandardCharsets.UTF_8)
                   .contentType(MediaType.APPLICATION_JSON)
                   .header(USER_ID_HEADER, 1L)
                   .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(content().json(mapper.writeValueAsString(List.of(itemRequestDto))));
    }

    @Test
    void getAllByUserTest_NotFoundException() throws Exception {
        Mockito.when(itemRequestService.getAllByUser(user.getId()))
               .thenThrow(NotFoundException.class);
        mvc.perform(get("/requests")
                   .characterEncoding(StandardCharsets.UTF_8)
                   .contentType(MediaType.APPLICATION_JSON)
                   .header(USER_ID_HEADER, 1L)
                   .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isNotFound());
    }

    @Test
    void getAllTest_Ok() throws Exception {
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

    @Test
    void getAllTest_NotFoundException() throws Exception {
        Mockito.when(itemRequestService.getAll(user.getId(), 0, 10))
               .thenThrow(NotFoundException.class);
        mvc.perform(get("/requests/all")
                   .characterEncoding(StandardCharsets.UTF_8)
                   .contentType(MediaType.APPLICATION_JSON)
                   .header(USER_ID_HEADER, 1L)
                   .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isNotFound());
    }

    @Test
    void getAllTest_ValidationException() throws Exception {
        Mockito.when(itemRequestService.getAll(user.getId(), 0, 10))
               .thenThrow(ValidationException.class);
        mvc.perform(get("/requests/all")
                   .characterEncoding(StandardCharsets.UTF_8)
                   .contentType(MediaType.APPLICATION_JSON)
                   .header(USER_ID_HEADER, 1L)
                   .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isBadRequest());
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