package ru.practicum.shareit.user;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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
import ru.practicum.shareit.user.service.UserServiceImpl;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {

    @Autowired
    ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    UserServiceImpl userService;

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private User user;

    @BeforeEach
    void setUp() {
        user = createUser(1);
    }

    @Test
    void createTest_Ok() throws Exception {
        Mockito.when(userService.create(user))
               .thenReturn(user);
        mvc.perform(post("/users")
                   .header(USER_ID_HEADER, user.getId())
                   .content(mapper.writeValueAsString(user))
                   .characterEncoding(StandardCharsets.UTF_8)
                   .contentType(MediaType.APPLICATION_JSON)
                   .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(content().json(mapper.writeValueAsString(user)));
    }

    @Test
    void createTest_ValidationException() throws Exception {
        Mockito.when(userService.create(user))
               .thenThrow(ValidationException.class);
        mvc.perform(post("/users")
                   .header(USER_ID_HEADER, user.getId())
                   .content(mapper.writeValueAsString(user))
                   .characterEncoding(StandardCharsets.UTF_8)
                   .contentType(MediaType.APPLICATION_JSON)
                   .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isBadRequest());
    }

    @Test
    void getAllTest_Ok() throws Exception {
        Mockito.when(userService.getAll())
               .thenReturn(List.of(user));
        mvc.perform(get("/users")
                   .characterEncoding(StandardCharsets.UTF_8)
                   .contentType(MediaType.APPLICATION_JSON)
                   .header(USER_ID_HEADER, user.getId())
                   .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(content().json(mapper.writeValueAsString(List.of(user))));
    }

    @Test
    void updateTest_Ok() throws Exception {
        Mockito.when(userService.update(Mockito.anyLong(), Mockito.any()))
               .thenReturn(user);
        mvc.perform(patch("/users/" + user.getId())
                   .content(mapper.writeValueAsString(user))
                   .characterEncoding(StandardCharsets.UTF_8)
                   .contentType(MediaType.APPLICATION_JSON)
                   .header(USER_ID_HEADER, user.getId())
                   .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(content().json(mapper.writeValueAsString(user)));
    }

    @Test
    void updateTest_NotFoundException() throws Exception {
        Mockito.when(userService.update(Mockito.anyLong(), Mockito.any()))
               .thenThrow(
                       NotFoundException.class);
        mvc.perform(patch("/users/" + user.getId())
                   .content(mapper.writeValueAsString(user))
                   .characterEncoding(StandardCharsets.UTF_8)
                   .contentType(MediaType.APPLICATION_JSON)
                   .header(USER_ID_HEADER, user.getId())
                   .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isNotFound());
    }

    @Test
    void deleteTest_Ok() throws Exception {
        mvc.perform(delete("/users/" + user.getId())
                   .characterEncoding(StandardCharsets.UTF_8)
                   .contentType(MediaType.APPLICATION_JSON)
                   .header(USER_ID_HEADER, user.getId())
                   .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk());
    }

    @Test
    void deleteTest_NotFoundException() throws Exception {
        Mockito.when(userService.delete(Mockito.anyLong()))
               .thenThrow(NotFoundException.class);
        mvc.perform(delete("/users/" + user.getId())
                   .characterEncoding(StandardCharsets.UTF_8)
                   .contentType(MediaType.APPLICATION_JSON)
                   .header(USER_ID_HEADER, user.getId())
                   .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isNotFound());
    }

    @Test
    void getByIdTest_Ok() throws Exception {
        Mockito.when(userService.getById(Mockito.anyLong()))
               .thenReturn(user);
        mvc.perform(get("/users/" + user.getId())
                   .characterEncoding(StandardCharsets.UTF_8)
                   .contentType(MediaType.APPLICATION_JSON)
                   .header(USER_ID_HEADER, user.getId())
                   .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(content().json(mapper.writeValueAsString(user)));
    }

    @Test
    void getByIdTest_NotFoundException() throws Exception {
        Mockito.when(userService.getById(Mockito.anyLong()))
               .thenThrow(NotFoundException.class);
        mvc.perform(get("/users/" + user.getId())
                   .characterEncoding(StandardCharsets.UTF_8)
                   .contentType(MediaType.APPLICATION_JSON)
                   .header(USER_ID_HEADER, user.getId())
                   .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isNotFound());
    }

    private User createUser(long id) {
        User user = new User();
        user.setId(id);
        user.setName("User " + id);
        user.setEmail("user_" + id + "@email.ru");
        return user;
    }
}