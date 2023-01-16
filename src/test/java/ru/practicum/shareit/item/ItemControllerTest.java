package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemServiceImpl;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {
    @Mock
    private ItemServiceImpl itemService = Mockito.mock(ItemServiceImpl.class);
    @InjectMocks
    private ItemController controller;
    private MockMvc mockMvc;

    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                                 .build();
    }

    @Test
    void getItemByIdTest() throws Exception {
        long itemId = 1;
        ItemDto expectedItemDto = getTestItemDto(itemId, "Item", "Description");

        Mockito.when(itemService.getItemById(Mockito.anyLong(), Mockito.anyLong()))
               .thenReturn(expectedItemDto);

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Sharer-User-Id", "1");

        mockMvc.perform(get("/items/" + itemId)
                       .headers(headers)
                       .characterEncoding(StandardCharsets.UTF_8)
                       .contentType(MediaType.APPLICATION_JSON)
                       .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id", is(expectedItemDto.getId()), Long.class))
               .andExpect(jsonPath("$.name", is(expectedItemDto.getName()), String.class))
               .andExpect(jsonPath("$.description", is(expectedItemDto.getDescription()), String.class))
               .andExpect(jsonPath("$.available", is(expectedItemDto.getAvailable()), Boolean.class));
    }

    @Test
    void getAllItemsTest() throws Exception {
        ItemDto item1 = getTestItemDto(1, "Item 1", "Description");
        ItemDto item2 = getTestItemDto(2, "Item 1", "Description");
        List<ItemDto> items = List.of(item1, item2);

        Mockito.when(itemService.getAllItems(Mockito.anyLong()))
               .thenReturn(items);

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Sharer-User-Id", "1");

        mockMvc.perform(get("/items")
                       .headers(headers)
                       .characterEncoding(StandardCharsets.UTF_8)
                       .contentType(MediaType.APPLICATION_JSON)
                       .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].id", is(item1.getId()), Long.class))
               .andExpect(jsonPath("$[0].name", is(item1.getName()), String.class))
               .andExpect(jsonPath("$[0].description", is(item1.getDescription()), String.class))
               .andExpect(jsonPath("$[0].available", is(item1.getAvailable()), Boolean.class))
               .andExpect(jsonPath("$[1].id", is(item2.getId()), Long.class))
               .andExpect(jsonPath("$[1].name", is(item2.getName()), String.class))
               .andExpect(jsonPath("$[1].description", is(item2.getDescription()), String.class))
               .andExpect(jsonPath("$[1].available", is(item2.getAvailable()), Boolean.class));
    }

    @Test
    void createItemTest() throws Exception {
        ItemDto request = ItemDto.builder()
                                 .withName("Item")
                                 .withDescription("Description")
                                 .withAvailable(true)
                                 .build();

        long itemId = 1;
        ItemDto expectedItemDto = ItemDto.builder()
                                         .withId(itemId)
                                         .withName("Item")
                                         .withDescription("Description")
                                         .withAvailable(true)
                                         .build();

        Mockito.when(itemService.createItem(Mockito.anyLong(), Mockito.any(ItemDto.class)))
               .thenReturn(expectedItemDto);

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Sharer-User-Id", "1");

        mockMvc.perform(post("/items")
                       .content(mapper.writeValueAsString(request))
                       .headers(headers)
                       .characterEncoding(StandardCharsets.UTF_8)
                       .contentType(MediaType.APPLICATION_JSON)
                       .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id", is(expectedItemDto.getId()), Long.class))
               .andExpect(jsonPath("$.name", is(expectedItemDto.getName()), String.class))
               .andExpect(jsonPath("$.description", is(expectedItemDto.getDescription()), String.class))
               .andExpect(jsonPath("$.available", is(expectedItemDto.getAvailable()), Boolean.class));
    }

    @Test
    void createCommentTest() throws Exception {
        LocalDateTime createdAt = LocalDateTime.now();
        CommentDto request = CommentDto.builder()
                                       .withText("Test comment")
                                       .build();

        long commentId = 1;
        CommentDto expectedCommentDto = CommentDto.builder()
                                                  .withId(commentId)
                                                  .withText("Test comment")
                                                  .withAuthorName("Author")
                                                  .withCreated(createdAt)
                                                  .build();

        Mockito.when(itemService.createComment(Mockito.anyLong(), Mockito.anyLong(), Mockito.any(CommentDto.class)))
               .thenReturn(expectedCommentDto);

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Sharer-User-Id", "1");

        mockMvc.perform(post("/items/1/comment")
                       .content(mapper.writeValueAsString(request))
                       .headers(headers)
                       .characterEncoding(StandardCharsets.UTF_8)
                       .contentType(MediaType.APPLICATION_JSON)
                       .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id", is(expectedCommentDto.getId()), Long.class))
               .andExpect(jsonPath("$.text", is(expectedCommentDto.getText()), String.class))
               .andExpect(jsonPath("$.authorName", is(expectedCommentDto.getAuthorName()), String.class))
               .andExpect(jsonPath("$.created[0]", is(createdAt.getYear()), Integer.class))
               .andExpect(jsonPath("$.created[1]", is(createdAt.getMonth().getValue()), Integer.class))
               .andExpect(jsonPath("$.created[2]", is(createdAt.getDayOfMonth()), Integer.class))
               .andExpect(jsonPath("$.created[3]", is(createdAt.getHour()), Integer.class))
               .andExpect(jsonPath("$.created[4]", is(createdAt.getMinute()), Integer.class))
               .andExpect(jsonPath("$.created[5]", is(createdAt.getSecond()), Integer.class))
               .andExpect(jsonPath("$.created[6]", is(createdAt.getNano()), Integer.class));
    }

    @Test
    void patchItemTest() throws Exception {
        long itemId = 1;
        ItemDto request = ItemDto.builder()
                                 .withId(itemId)
                                 .withName("Item")
                                 .withDescription("Description")
                                 .withAvailable(false)
                                 .build();

        ItemDto expectedItemDto = ItemDto.builder()
                                         .withId(itemId)
                                         .withName("Item")
                                         .withDescription("Description")
                                         .withAvailable(false)
                                         .build();

        Mockito.when(itemService.patchItem(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyMap()))
               .thenReturn(expectedItemDto);

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Sharer-User-Id", "1");

        mockMvc.perform(patch("/items/1")
                       .content(mapper.writeValueAsString(request))
                       .headers(headers)
                       .characterEncoding(StandardCharsets.UTF_8)
                       .contentType(MediaType.APPLICATION_JSON)
                       .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id", is(expectedItemDto.getId()), Long.class))
               .andExpect(jsonPath("$.name", is(expectedItemDto.getName()), String.class))
               .andExpect(jsonPath("$.description", is(expectedItemDto.getDescription()), String.class))
               .andExpect(jsonPath("$.available", is(expectedItemDto.getAvailable()), Boolean.class));
    }

    @Test
    void searchItemTest() throws Exception {
        long itemId = 1;
        ItemDto expectedItemDto = ItemDto.builder()
                                         .withId(itemId)
                                         .withName("Item")
                                         .withDescription("Description")
                                         .withAvailable(false)
                                         .build();

        Mockito.when(itemService.searchItem(Mockito.anyString(), Mockito.anyLong()))
               .thenReturn(List.of(expectedItemDto));

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Sharer-User-Id", "1");

        mockMvc.perform(get("/items/search?text=item")
                       .headers(headers)
                       .characterEncoding(StandardCharsets.UTF_8)
                       .contentType(MediaType.APPLICATION_JSON)
                       .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].id", is(expectedItemDto.getId()), Long.class))
               .andExpect(jsonPath("$[0].name", is(expectedItemDto.getName()), String.class))
               .andExpect(jsonPath("$[0].description", is(expectedItemDto.getDescription()), String.class))
               .andExpect(jsonPath("$[0].available", is(expectedItemDto.getAvailable()), Boolean.class));
    }

    private ItemDto getTestItemDto(long id, String name, String description) {
        return ItemDto.builder()
                      .withId(id)
                      .withName(name)
                      .withDescription(description)
                      .build();
    }
}