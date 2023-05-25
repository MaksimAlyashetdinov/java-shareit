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
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
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

    @Test
    void createTest() throws Exception {
        User user = createUser(1);
        Mockito.when(userService.create(user)).thenReturn(user);
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
    void getAllTest() throws Exception {
        User user = createUser(1);
        Mockito.when(userService.getAll()).thenReturn(List.of(user));
        mvc.perform(get("/users")
                   .characterEncoding(StandardCharsets.UTF_8)
                   .contentType(MediaType.APPLICATION_JSON)
                   .header(USER_ID_HEADER, user.getId())
                   .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(content().json(mapper.writeValueAsString(List.of(user))));
    }

    @Test
    void updateTest() throws Exception {
        User user = createUser(1);
        Mockito.when(userService.update(Mockito.anyLong(), Mockito.any())).thenReturn(user);
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
    void deleteTest() throws Exception {
        User user = createUser(1);
        mvc.perform(delete("/users/" + user.getId())
                   .characterEncoding(StandardCharsets.UTF_8)
                   .contentType(MediaType.APPLICATION_JSON)
                   .header(USER_ID_HEADER, user.getId())
                   .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk());
    }

    @Test
    void getByIdTest() throws Exception {
        User user = createUser(1);
        Mockito.when(userService.getById(Mockito.anyLong())).thenReturn(user);
        mvc.perform(get("/users/" + user.getId())
                   .characterEncoding(StandardCharsets.UTF_8)
                   .contentType(MediaType.APPLICATION_JSON)
                   .header(USER_ID_HEADER, user.getId())
                   .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(content().json(mapper.writeValueAsString(user)));
    }

    private User createUser(long id) {
        User user = new User();
        user.setId(id);
        user.setName("User " + id);
        user.setEmail("user_" + id + "@email.ru");
        return user;
    }
}