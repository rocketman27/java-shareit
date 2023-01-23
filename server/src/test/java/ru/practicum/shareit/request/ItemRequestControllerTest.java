package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
class ItemRequestControllerTest {
    @Mock
    private ItemRequestServiceImpl itemRequestService;
    @InjectMocks
    private ItemRequestController controller;
    private MockMvc mockMvc;
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());


    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                                 .build();
    }

    @Test
    void shouldReturnItemRequestById() throws Exception {
        LocalDateTime createdAt = LocalDateTime.now();
        long itemRequestId = 1;
        ItemRequestDto itemRequestDto = getItemRequestDto(itemRequestId, createdAt);

        Mockito.when(itemRequestService.getItemRequestById(Mockito.anyLong(), Mockito.anyLong()))
               .thenReturn(itemRequestDto);

        HttpHeaders headers = new HttpHeaders();
        addUserIdToHeaders(headers, 1);

        mockMvc.perform(get("/requests/1")
                       .headers(headers)
                       .characterEncoding(StandardCharsets.UTF_8)
                       .contentType(MediaType.APPLICATION_JSON)
                       .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
               .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription()), String.class))
               .andExpect(jsonPath("$.created[0]", is(createdAt.getYear()), Integer.class))
               .andExpect(jsonPath("$.created[1]", is(createdAt.getMonth().getValue()), Integer.class))
               .andExpect(jsonPath("$.created[2]", is(createdAt.getDayOfMonth()), Integer.class))
               .andExpect(jsonPath("$.created[3]", is(createdAt.getHour()), Integer.class))
               .andExpect(jsonPath("$.created[4]", is(createdAt.getMinute()), Integer.class))
               .andExpect(jsonPath("$.created[5]", is(createdAt.getSecond()), Integer.class))
               .andExpect(jsonPath("$.created[6]", is(createdAt.getNano()), Integer.class))
               .andExpect(jsonPath("$.items", is(itemRequestDto.getItems()), List.class));
    }

    @Test
    void shouldReturnAllItemRequestsByUserId() throws Exception {
        LocalDateTime createdAt = LocalDateTime.now();

        List<ItemRequestDto> itemRequestDto = List.of(
                getItemRequestDto(1, createdAt),
                getItemRequestDto(2, createdAt)
        );

        Mockito.when(itemRequestService.getItemRequestsByUserId(Mockito.anyLong()))
               .thenReturn(itemRequestDto);

        HttpHeaders headers = new HttpHeaders();
        addUserIdToHeaders(headers, 1);

        mockMvc.perform(get("/requests")
                       .headers(headers)
                       .characterEncoding(StandardCharsets.UTF_8)
                       .contentType(MediaType.APPLICATION_JSON)
                       .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].id", is(itemRequestDto.get(0).getId()), Long.class))
               .andExpect(jsonPath("$[0].description", is(itemRequestDto.get(0).getDescription()), String.class))
               .andExpect(jsonPath("$[0].created[0]", is(createdAt.getYear()), Integer.class))
               .andExpect(jsonPath("$[0].created[1]", is(createdAt.getMonth().getValue()), Integer.class))
               .andExpect(jsonPath("$[0].created[2]", is(createdAt.getDayOfMonth()), Integer.class))
               .andExpect(jsonPath("$[0].created[3]", is(createdAt.getHour()), Integer.class))
               .andExpect(jsonPath("$[0].created[4]", is(createdAt.getMinute()), Integer.class))
               .andExpect(jsonPath("$[0].created[5]", is(createdAt.getSecond()), Integer.class))
               .andExpect(jsonPath("$[0].created[6]", is(createdAt.getNano()), Integer.class))
               .andExpect(jsonPath("$[0].items", is(itemRequestDto.get(0).getItems()), List.class))
               .andExpect(jsonPath("$[0].id", is(itemRequestDto.get(0).getId()), Long.class))
               .andExpect(jsonPath("$[1].description", is(itemRequestDto.get(1).getDescription()), String.class))
               .andExpect(jsonPath("$[1].created[0]", is(createdAt.getYear()), Integer.class))
               .andExpect(jsonPath("$[1].created[1]", is(createdAt.getMonth().getValue()), Integer.class))
               .andExpect(jsonPath("$[1].created[2]", is(createdAt.getDayOfMonth()), Integer.class))
               .andExpect(jsonPath("$[1].created[3]", is(createdAt.getHour()), Integer.class))
               .andExpect(jsonPath("$[1].created[4]", is(createdAt.getMinute()), Integer.class))
               .andExpect(jsonPath("$[1].created[5]", is(createdAt.getSecond()), Integer.class))
               .andExpect(jsonPath("$[1].created[6]", is(createdAt.getNano()), Integer.class))
               .andExpect(jsonPath("$[1].items", is(itemRequestDto.get(1).getItems()), List.class));
    }

    @Test
    void shouldReturnAllItemRequests() throws Exception {
        LocalDateTime createdAt = LocalDateTime.now();

        List<ItemRequestDto> itemRequestDto = List.of(
                getItemRequestDto(1, createdAt),
                getItemRequestDto(2, createdAt)
        );

        Mockito.when(itemRequestService.getAllItemRequests(Mockito.anyLong()))
               .thenReturn(itemRequestDto);

        HttpHeaders headers = new HttpHeaders();
        addUserIdToHeaders(headers, 1);

        mockMvc.perform(get("/requests/all")
                       .headers(headers)
                       .characterEncoding(StandardCharsets.UTF_8)
                       .contentType(MediaType.APPLICATION_JSON)
                       .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].id", is(itemRequestDto.get(0).getId()), Long.class))
               .andExpect(jsonPath("$[0].description", is(itemRequestDto.get(0).getDescription()), String.class))
               .andExpect(jsonPath("$[0].created[0]", is(createdAt.getYear()), Integer.class))
               .andExpect(jsonPath("$[0].created[1]", is(createdAt.getMonth().getValue()), Integer.class))
               .andExpect(jsonPath("$[0].created[2]", is(createdAt.getDayOfMonth()), Integer.class))
               .andExpect(jsonPath("$[0].created[3]", is(createdAt.getHour()), Integer.class))
               .andExpect(jsonPath("$[0].created[4]", is(createdAt.getMinute()), Integer.class))
               .andExpect(jsonPath("$[0].created[5]", is(createdAt.getSecond()), Integer.class))
               .andExpect(jsonPath("$[0].created[6]", is(createdAt.getNano()), Integer.class))
               .andExpect(jsonPath("$[0].items", is(itemRequestDto.get(0).getItems()), List.class))
               .andExpect(jsonPath("$[0].id", is(itemRequestDto.get(0).getId()), Long.class))
               .andExpect(jsonPath("$[1].description", is(itemRequestDto.get(1).getDescription()), String.class))
               .andExpect(jsonPath("$[1].created[0]", is(createdAt.getYear()), Integer.class))
               .andExpect(jsonPath("$[1].created[1]", is(createdAt.getMonth().getValue()), Integer.class))
               .andExpect(jsonPath("$[1].created[2]", is(createdAt.getDayOfMonth()), Integer.class))
               .andExpect(jsonPath("$[1].created[3]", is(createdAt.getHour()), Integer.class))
               .andExpect(jsonPath("$[1].created[4]", is(createdAt.getMinute()), Integer.class))
               .andExpect(jsonPath("$[1].created[5]", is(createdAt.getSecond()), Integer.class))
               .andExpect(jsonPath("$[1].created[6]", is(createdAt.getNano()), Integer.class))
               .andExpect(jsonPath("$[1].items", is(itemRequestDto.get(1).getItems()), List.class));
    }

    @Test
    void shouldCreateItemRequest() throws Exception {
        LocalDateTime createdAt = LocalDateTime.now();
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                                                      .withDescription("Description")
                                                      .build();

        ItemRequestDto expectedItemRequestDto = getItemRequestDto(1, createdAt);

        Mockito.when(itemRequestService.createItemRequest(Mockito.anyLong(), Mockito.any(ItemRequestDto.class)))
               .thenReturn(expectedItemRequestDto);

        HttpHeaders headers = new HttpHeaders();
        addUserIdToHeaders(headers, 1);

        mockMvc.perform(post("/requests")
                       .headers(headers)
                       .content(mapper.writeValueAsString(itemRequestDto))
                       .characterEncoding(StandardCharsets.UTF_8)
                       .contentType(MediaType.APPLICATION_JSON)
                       .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id", is(expectedItemRequestDto.getId()), Long.class))
               .andExpect(jsonPath("$.description", is(expectedItemRequestDto.getDescription()), String.class))
               .andExpect(jsonPath("$.created[0]", is(createdAt.getYear()), Integer.class))
               .andExpect(jsonPath("$.created[1]", is(createdAt.getMonth().getValue()), Integer.class))
               .andExpect(jsonPath("$.created[2]", is(createdAt.getDayOfMonth()), Integer.class))
               .andExpect(jsonPath("$.created[3]", is(createdAt.getHour()), Integer.class))
               .andExpect(jsonPath("$.created[4]", is(createdAt.getMinute()), Integer.class))
               .andExpect(jsonPath("$.created[5]", is(createdAt.getSecond()), Integer.class))
               .andExpect(jsonPath("$.created[6]", is(createdAt.getNano()), Integer.class))
               .andExpect(jsonPath("$.items", is(expectedItemRequestDto.getItems()), List.class));

    }

    private ItemRequestDto getItemRequestDto(long id, LocalDateTime createdAt) {
        return ItemRequestDto.builder()
                             .withId(id)
                             .withDescription("Description")
                             .withCreated(createdAt)
                             .withItems(Collections.emptyList())
                             .build();
    }

    private void addUserIdToHeaders(HttpHeaders headers, long id) {
        headers.add("X-Sharer-User-Id", String.valueOf(id));
    }
}
