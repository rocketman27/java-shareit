package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    @Mock
    private UserServiceImpl userService = Mockito.mock(UserServiceImpl.class);
    @InjectMocks
    private UserController controller;
    private MockMvc mockMvc;

    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                                 .build();
    }

    @Test
    void getUserByIdTest() throws Exception {
        long userId = 1;
        UserDto expectedUserDto = getTestUserDto(userId, "Test User", "test@gmail.com");

        Mockito.when(userService.getUserById(Mockito.anyLong()))
               .thenReturn(expectedUserDto);

        mockMvc.perform(get("/users/" + userId)
                       .characterEncoding(StandardCharsets.UTF_8)
                       .contentType(MediaType.APPLICATION_JSON)
                       .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id", is(expectedUserDto.getId()), Long.class))
               .andExpect(jsonPath("$.name", is(expectedUserDto.getName()), String.class))
               .andExpect(jsonPath("$.email", is(expectedUserDto.getEmail()), String.class));
    }

    @Test
    void getAllUsers() throws Exception {
        UserDto user1 = getTestUserDto(1, "Test User 1", "test1@gmail.com");
        UserDto user2 = getTestUserDto(2, "Test User 2", "test2@gmail.com");
        List<UserDto> users = List.of(user1, user2);

        Mockito.when(userService.getUsers())
               .thenReturn(users);

        mockMvc.perform(get("/users")
                       .characterEncoding(StandardCharsets.UTF_8)
                       .contentType(MediaType.APPLICATION_JSON)
                       .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].id", is(user1.getId()), Long.class))
               .andExpect(jsonPath("$[0].name", is(user1.getName()), String.class))
               .andExpect(jsonPath("$[0].email", is(user1.getEmail()), String.class))
               .andExpect(jsonPath("$[1].id", is(user2.getId()), Long.class))
               .andExpect(jsonPath("$[1].name", is(user2.getName()), String.class))
               .andExpect(jsonPath("$[1].email", is(user2.getEmail()), String.class));
    }

    @Test
    void createUserTest() throws Exception {
        UserDto request = UserDto.builder()
                                 .withName("Test User")
                                 .withEmail("email@gmail.com")
                                 .build();

        long userId = 1;
        UserDto expectedUserDto = getTestUserDto(userId, "Test User", "test@gmail.com");

        Mockito.when(userService.createUser(Mockito.any(UserDto.class)))
               .thenReturn(expectedUserDto);

        mockMvc.perform(post("/users")
                       .content(mapper.writeValueAsString(request))
                       .characterEncoding(StandardCharsets.UTF_8)
                       .contentType(MediaType.APPLICATION_JSON)
                       .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id", is(expectedUserDto.getId()), Long.class))
               .andExpect(jsonPath("$.name", is(expectedUserDto.getName()), String.class))
               .andExpect(jsonPath("$.email", is(expectedUserDto.getEmail()), String.class));
    }

    @Test
    void patchUserTest() throws Exception {
        UserDto request = UserDto.builder()
                                 .withEmail("update@gmail.com")
                                 .build();

        long userId = 1;
        UserDto expectedUserDto = getTestUserDto(userId, "Test User", "update@gmail.com");

        Mockito.when(userService.patchUser(Mockito.anyLong(), Mockito.anyMap()))
               .thenReturn(expectedUserDto);

        mockMvc.perform(patch("/users/" + userId)
                       .content(mapper.writeValueAsString(request))
                       .characterEncoding(StandardCharsets.UTF_8)
                       .contentType(MediaType.APPLICATION_JSON)
                       .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id", is(expectedUserDto.getId()), Long.class))
               .andExpect(jsonPath("$.name", is(expectedUserDto.getName()), String.class))
               .andExpect(jsonPath("$.email", is(expectedUserDto.getEmail()), String.class));
    }

    private static UserDto getTestUserDto(long id, String name, String email) {
        return UserDto.builder()
                      .withId(id)
                      .withName(name)
                      .withEmail(email)
                      .build();
    }
}
