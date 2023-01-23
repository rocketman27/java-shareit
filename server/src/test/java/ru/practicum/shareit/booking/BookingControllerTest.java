package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingServiceImpl;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static java.time.LocalDateTime.now;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.booking.model.BookingStatus.*;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {
    @Mock
    private BookingServiceImpl bookingService;
    @InjectMocks
    private BookingController controller;
    private MockMvc mockMvc;
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                                 .build();
    }

    @Test
    void createBookingTest() throws Exception {
        LocalDateTime start = now().plusMinutes(1);
        LocalDateTime end = start.plusMinutes(10);

        BookingDto body = BookingDto.builder()
                                    .withItemId(1)
                                    .withStart(start)
                                    .withEnd(end)
                                    .build();

        BookingDto expectedBookingDto = getBookingDto(1, start, end, WAITING);

        Mockito.when(bookingService.createBooking(Mockito.anyLong(), Mockito.any(BookingDto.class)))
               .thenReturn(expectedBookingDto);

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Sharer-User-Id", "1");

        mockMvc.perform(post("/bookings")
                       .headers(headers)
                       .content(mapper.writeValueAsString(body))
                       .characterEncoding(StandardCharsets.UTF_8)
                       .contentType(MediaType.APPLICATION_JSON)
                       .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id", is(expectedBookingDto.getId()), Long.class))
               .andExpect(jsonPath("$.booker.id", is(expectedBookingDto.getBooker().getId()), Long.class))
               .andExpect(jsonPath("$.status", is(expectedBookingDto.getStatus().name()), BookingStatus.class))
               .andExpect(jsonPath("$.start[0]", is(start.getYear()), Integer.class))
               .andExpect(jsonPath("$.start[1]", is(start.getMonth().getValue()), Integer.class))
               .andExpect(jsonPath("$.start[2]", is(start.getDayOfMonth()), Integer.class))
               .andExpect(jsonPath("$.start[3]", is(start.getHour()), Integer.class))
               .andExpect(jsonPath("$.start[4]", is(start.getMinute()), Integer.class))
               .andExpect(jsonPath("$.start[5]", is(start.getSecond()), Integer.class))
               .andExpect(jsonPath("$.start[6]", is(start.getNano()), Integer.class))
               .andExpect(jsonPath("$.end[0]", is(end.getYear()), Integer.class))
               .andExpect(jsonPath("$.end[1]", is(end.getMonth().getValue()), Integer.class))
               .andExpect(jsonPath("$.end[2]", is(end.getDayOfMonth()), Integer.class))
               .andExpect(jsonPath("$.end[3]", is(end.getHour()), Integer.class))
               .andExpect(jsonPath("$.end[4]", is(end.getMinute()), Integer.class))
               .andExpect(jsonPath("$.end[5]", is(end.getSecond()), Integer.class))
               .andExpect(jsonPath("$.end[6]", is(end.getNano()), Integer.class))
               .andExpect(jsonPath("$.item.id", is(expectedBookingDto.getItem().getId()), Long.class))
               .andExpect(jsonPath("$.item.name", is(expectedBookingDto.getItem().getName()), String.class));
    }

    @Disabled
    @ParameterizedTest()
    @MethodSource("provideInvalidRequests")
    void createBookingWithInvalidData(BookingDto request) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Sharer-User-Id", "1");

        mockMvc.perform(post("/bookings")
                       .headers(headers)
                       .content(mapper.writeValueAsString(request))
                       .characterEncoding(StandardCharsets.UTF_8)
                       .contentType(MediaType.APPLICATION_JSON)
                       .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isBadRequest());
    }

    private static Stream<Arguments> provideInvalidRequests() {
        return Stream.of(
                Arguments.of(Named.of("Start is null",
                        BookingDto.builder()
                                  .withItemId(1)
                                  .withStart(null)
                                  .withEnd(now().plusMinutes(10))
                                  .build())),
                Arguments.of(Named.of("End is null",
                        BookingDto.builder()
                                  .withItemId(1)
                                  .withStart(now().plusMinutes(1))
                                  .withEnd(null)
                                  .build())),
                Arguments.of(Named.of("Start is in the past",
                        BookingDto.builder()
                                  .withStart(now().minusMinutes(1))
                                  .withEnd(now().plusMinutes(10))
                                  .build())),
                Arguments.of(Named.of("End is before start",
                        BookingDto.builder()
                                  .withStart(now().plusMinutes(1))
                                  .withEnd(now().minusMinutes(2))
                                  .build())));
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void approveOrRejectBookingTest(boolean approved) throws Exception {
        LocalDateTime start = now().plusMinutes(1);
        LocalDateTime end = start.plusMinutes(10);

        BookingDto expectedBookingDto = getBookingDto(1, start, end, approved ? APPROVED : REJECTED);

        Mockito.when(bookingService.approveOrRejectBooking(Mockito.anyLong(), Mockito.anyBoolean(), Mockito.anyLong()))
               .thenReturn(expectedBookingDto);

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Sharer-User-Id", "1");

        mockMvc.perform(patch("/bookings/1?approved=" + approved)
                       .headers(headers)
                       .characterEncoding(StandardCharsets.UTF_8)
                       .contentType(MediaType.APPLICATION_JSON)
                       .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id", is(expectedBookingDto.getId()), Long.class))
               .andExpect(jsonPath("$.booker.id", is(expectedBookingDto.getBooker().getId()), Long.class))
               .andExpect(jsonPath("$.status", is(expectedBookingDto.getStatus().name()), BookingStatus.class))
               .andExpect(jsonPath("$.start[0]", is(start.getYear()), Integer.class))
               .andExpect(jsonPath("$.start[1]", is(start.getMonth().getValue()), Integer.class))
               .andExpect(jsonPath("$.start[2]", is(start.getDayOfMonth()), Integer.class))
               .andExpect(jsonPath("$.start[3]", is(start.getHour()), Integer.class))
               .andExpect(jsonPath("$.start[4]", is(start.getMinute()), Integer.class))
               .andExpect(jsonPath("$.start[5]", is(start.getSecond()), Integer.class))
               .andExpect(jsonPath("$.start[6]", is(start.getNano()), Integer.class))
               .andExpect(jsonPath("$.end[0]", is(end.getYear()), Integer.class))
               .andExpect(jsonPath("$.end[1]", is(end.getMonth().getValue()), Integer.class))
               .andExpect(jsonPath("$.end[2]", is(end.getDayOfMonth()), Integer.class))
               .andExpect(jsonPath("$.end[3]", is(end.getHour()), Integer.class))
               .andExpect(jsonPath("$.end[4]", is(end.getMinute()), Integer.class))
               .andExpect(jsonPath("$.end[5]", is(end.getSecond()), Integer.class))
               .andExpect(jsonPath("$.end[6]", is(end.getNano()), Integer.class))
               .andExpect(jsonPath("$.item.id", is(expectedBookingDto.getItem().getId()), Long.class));
    }

    @Test
    void getBookingByIdTest() throws Exception {
        LocalDateTime start = now().plusMinutes(1);
        LocalDateTime end = start.plusMinutes(10);

        BookingDto expectedBookingDto = getBookingDto(1, start, end, WAITING);

        Mockito.when(bookingService.getBookingById(Mockito.anyLong(), Mockito.anyLong()))
               .thenReturn(expectedBookingDto);

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Sharer-User-Id", "1");

        mockMvc.perform(get("/bookings/1")
                       .headers(headers)
                       .characterEncoding(StandardCharsets.UTF_8)
                       .contentType(MediaType.APPLICATION_JSON)
                       .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id", is(expectedBookingDto.getId()), Long.class))
               .andExpect(jsonPath("$.booker.id", is(expectedBookingDto.getBooker().getId()), Long.class))
               .andExpect(jsonPath("$.status", is(expectedBookingDto.getStatus().name()), BookingStatus.class))
               .andExpect(jsonPath("$.start[0]", is(start.getYear()), Integer.class))
               .andExpect(jsonPath("$.start[1]", is(start.getMonth().getValue()), Integer.class))
               .andExpect(jsonPath("$.start[2]", is(start.getDayOfMonth()), Integer.class))
               .andExpect(jsonPath("$.start[3]", is(start.getHour()), Integer.class))
               .andExpect(jsonPath("$.start[4]", is(start.getMinute()), Integer.class))
               .andExpect(jsonPath("$.start[5]", is(start.getSecond()), Integer.class))
               .andExpect(jsonPath("$.start[6]", is(start.getNano()), Integer.class))
               .andExpect(jsonPath("$.end[0]", is(end.getYear()), Integer.class))
               .andExpect(jsonPath("$.end[1]", is(end.getMonth().getValue()), Integer.class))
               .andExpect(jsonPath("$.end[2]", is(end.getDayOfMonth()), Integer.class))
               .andExpect(jsonPath("$.end[3]", is(end.getHour()), Integer.class))
               .andExpect(jsonPath("$.end[4]", is(end.getMinute()), Integer.class))
               .andExpect(jsonPath("$.end[5]", is(end.getSecond()), Integer.class))
               .andExpect(jsonPath("$.end[6]", is(end.getNano()), Integer.class))
               .andExpect(jsonPath("$.item.id", is(expectedBookingDto.getItem().getId()), Long.class))
               .andExpect(jsonPath("$.item.name", is(expectedBookingDto.getItem().getName()), String.class));
    }

    @Test
    void getAllBookingsByOwnerTest() throws Exception {
        BookingDto booking1 = getBookingDto(1, now().plusMinutes(1), now().minusMinutes(2), APPROVED);
        BookingDto booking2 = getBookingDto(2, now().plusMinutes(3), now().minusMinutes(4), APPROVED);

        List<BookingDto> bookings = List.of(booking1, booking2);

        Mockito.when(bookingService.getAllBookingsByOwner(Mockito.anyString(), Mockito.anyLong()))
               .thenReturn(bookings);

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Sharer-User-Id", "1");

        mockMvc.perform(get("/bookings/owner")
                       .headers(headers)
                       .characterEncoding(StandardCharsets.UTF_8)
                       .contentType(MediaType.APPLICATION_JSON)
                       .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].id", is(bookings.get(0).getId()), Long.class))
               .andExpect(jsonPath("$[0].booker.id", is(bookings.get(0).getBooker().getId()), Long.class))
               .andExpect(jsonPath("$[0].status", is(bookings.get(0).getStatus().name()), BookingStatus.class))
               .andExpect(jsonPath("$[0].start[0]", is(bookings.get(0).getStart().getYear()), Integer.class))
               .andExpect(jsonPath("$[0].start[1]", is(bookings.get(0).getStart().getMonth().getValue()), Integer.class))
               .andExpect(jsonPath("$[0].start[2]", is(bookings.get(0).getStart().getDayOfMonth()), Integer.class))
               .andExpect(jsonPath("$[0].start[3]", is(bookings.get(0).getStart().getHour()), Integer.class))
               .andExpect(jsonPath("$[0].start[4]", is(bookings.get(0).getStart().getMinute()), Integer.class))
               .andExpect(jsonPath("$[0].start[5]", is(bookings.get(0).getStart().getSecond()), Integer.class))
               .andExpect(jsonPath("$[0].start[6]", is(bookings.get(0).getStart().getNano()), Integer.class))
               .andExpect(jsonPath("$[0].end[0]", is(bookings.get(0).getEnd().getYear()), Integer.class))
               .andExpect(jsonPath("$[0].end[1]", is(bookings.get(0).getEnd().getMonth().getValue()), Integer.class))
               .andExpect(jsonPath("$[0].end[2]", is(bookings.get(0).getEnd().getDayOfMonth()), Integer.class))
               .andExpect(jsonPath("$[0].end[3]", is(bookings.get(0).getEnd().getHour()), Integer.class))
               .andExpect(jsonPath("$[0].end[4]", is(bookings.get(0).getEnd().getMinute()), Integer.class))
               .andExpect(jsonPath("$[0].end[5]", is(bookings.get(0).getEnd().getSecond()), Integer.class))
               .andExpect(jsonPath("$[0].end[6]", is(bookings.get(0).getEnd().getNano()), Integer.class))
               .andExpect(jsonPath("$[0].item.id", is(bookings.get(0).getItem().getId()), Long.class))
               .andExpect(jsonPath("$[0].item.name", is(bookings.get(0).getItem().getName()), String.class))
               .andExpect(jsonPath("$[1].id", is(bookings.get(1).getId()), Long.class))
               .andExpect(jsonPath("$[1].booker.id", is(bookings.get(1).getBooker().getId()), Long.class))
               .andExpect(jsonPath("$[1].status", is(bookings.get(1).getStatus().name()), BookingStatus.class))
               .andExpect(jsonPath("$[1].start[0]", is(bookings.get(1).getStart().getYear()), Integer.class))
               .andExpect(jsonPath("$[1].start[1]", is(bookings.get(1).getStart().getMonth().getValue()), Integer.class))
               .andExpect(jsonPath("$[1].start[2]", is(bookings.get(1).getStart().getDayOfMonth()), Integer.class))
               .andExpect(jsonPath("$[1].start[3]", is(bookings.get(1).getStart().getHour()), Integer.class))
               .andExpect(jsonPath("$[1].start[4]", is(bookings.get(1).getStart().getMinute()), Integer.class))
               .andExpect(jsonPath("$[1].start[5]", is(bookings.get(1).getStart().getSecond()), Integer.class))
               .andExpect(jsonPath("$[1].start[6]", is(bookings.get(1).getStart().getNano()), Integer.class))
               .andExpect(jsonPath("$[1].end[0]", is(bookings.get(1).getEnd().getYear()), Integer.class))
               .andExpect(jsonPath("$[1].end[1]", is(bookings.get(1).getEnd().getMonth().getValue()), Integer.class))
               .andExpect(jsonPath("$[1].end[2]", is(bookings.get(1).getEnd().getDayOfMonth()), Integer.class))
               .andExpect(jsonPath("$[1].end[3]", is(bookings.get(1).getEnd().getHour()), Integer.class))
               .andExpect(jsonPath("$[1].end[4]", is(bookings.get(1).getEnd().getMinute()), Integer.class))
               .andExpect(jsonPath("$[1].end[5]", is(bookings.get(1).getEnd().getSecond()), Integer.class))
               .andExpect(jsonPath("$[1].end[6]", is(bookings.get(1).getEnd().getNano()), Integer.class))
               .andExpect(jsonPath("$[1].item.id", is(bookings.get(1).getItem().getId()), Long.class))
               .andExpect(jsonPath("$[1].item.name", is(bookings.get(1).getItem().getName()), String.class));
    }

    @Test
    void getAllBookingsByBookerTest() throws Exception {
        BookingDto booking1 = getBookingDto(1, now().plusMinutes(1), now().minusMinutes(2), APPROVED);
        BookingDto booking2 = getBookingDto(2, now().plusMinutes(3), now().minusMinutes(4), APPROVED);

        List<BookingDto> bookings = List.of(booking1, booking2);

        Mockito.when(bookingService.getAllBookingsByBooker(Mockito.anyString(), Mockito.anyLong()))
               .thenReturn(bookings);

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Sharer-User-Id", "1");

        mockMvc.perform(get("/bookings")
                       .headers(headers)
                       .characterEncoding(StandardCharsets.UTF_8)
                       .contentType(MediaType.APPLICATION_JSON)
                       .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].id", is(bookings.get(0).getId()), Long.class))
               .andExpect(jsonPath("$[0].booker.id", is(bookings.get(0).getBooker().getId()), Long.class))
               .andExpect(jsonPath("$[0].status", is(bookings.get(0).getStatus().name()), BookingStatus.class))
               .andExpect(jsonPath("$[0].start[0]", is(bookings.get(0).getStart().getYear()), Integer.class))
               .andExpect(jsonPath("$[0].start[1]", is(bookings.get(0).getStart().getMonth().getValue()), Integer.class))
               .andExpect(jsonPath("$[0].start[2]", is(bookings.get(0).getStart().getDayOfMonth()), Integer.class))
               .andExpect(jsonPath("$[0].start[3]", is(bookings.get(0).getStart().getHour()), Integer.class))
               .andExpect(jsonPath("$[0].start[4]", is(bookings.get(0).getStart().getMinute()), Integer.class))
               .andExpect(jsonPath("$[0].start[5]", is(bookings.get(0).getStart().getSecond()), Integer.class))
               .andExpect(jsonPath("$[0].start[6]", is(bookings.get(0).getStart().getNano()), Integer.class))
               .andExpect(jsonPath("$[0].end[0]", is(bookings.get(0).getEnd().getYear()), Integer.class))
               .andExpect(jsonPath("$[0].end[1]", is(bookings.get(0).getEnd().getMonth().getValue()), Integer.class))
               .andExpect(jsonPath("$[0].end[2]", is(bookings.get(0).getEnd().getDayOfMonth()), Integer.class))
               .andExpect(jsonPath("$[0].end[3]", is(bookings.get(0).getEnd().getHour()), Integer.class))
               .andExpect(jsonPath("$[0].end[4]", is(bookings.get(0).getEnd().getMinute()), Integer.class))
               .andExpect(jsonPath("$[0].end[5]", is(bookings.get(0).getEnd().getSecond()), Integer.class))
               .andExpect(jsonPath("$[0].end[6]", is(bookings.get(0).getEnd().getNano()), Integer.class))
               .andExpect(jsonPath("$[0].item.id", is(bookings.get(0).getItem().getId()), Long.class))
               .andExpect(jsonPath("$[0].item.name", is(bookings.get(0).getItem().getName()), String.class))
               .andExpect(jsonPath("$[1].id", is(bookings.get(1).getId()), Long.class))
               .andExpect(jsonPath("$[1].booker.id", is(bookings.get(1).getBooker().getId()), Long.class))
               .andExpect(jsonPath("$[1].status", is(bookings.get(1).getStatus().name()), BookingStatus.class))
               .andExpect(jsonPath("$[1].start[0]", is(bookings.get(1).getStart().getYear()), Integer.class))
               .andExpect(jsonPath("$[1].start[1]", is(bookings.get(1).getStart().getMonth().getValue()), Integer.class))
               .andExpect(jsonPath("$[1].start[2]", is(bookings.get(1).getStart().getDayOfMonth()), Integer.class))
               .andExpect(jsonPath("$[1].start[3]", is(bookings.get(1).getStart().getHour()), Integer.class))
               .andExpect(jsonPath("$[1].start[4]", is(bookings.get(1).getStart().getMinute()), Integer.class))
               .andExpect(jsonPath("$[1].start[5]", is(bookings.get(1).getStart().getSecond()), Integer.class))
               .andExpect(jsonPath("$[1].start[6]", is(bookings.get(1).getStart().getNano()), Integer.class))
               .andExpect(jsonPath("$[1].end[0]", is(bookings.get(1).getEnd().getYear()), Integer.class))
               .andExpect(jsonPath("$[1].end[1]", is(bookings.get(1).getEnd().getMonth().getValue()), Integer.class))
               .andExpect(jsonPath("$[1].end[2]", is(bookings.get(1).getEnd().getDayOfMonth()), Integer.class))
               .andExpect(jsonPath("$[1].end[3]", is(bookings.get(1).getEnd().getHour()), Integer.class))
               .andExpect(jsonPath("$[1].end[4]", is(bookings.get(1).getEnd().getMinute()), Integer.class))
               .andExpect(jsonPath("$[1].end[5]", is(bookings.get(1).getEnd().getSecond()), Integer.class))
               .andExpect(jsonPath("$[1].end[6]", is(bookings.get(1).getEnd().getNano()), Integer.class))
               .andExpect(jsonPath("$[1].item.id", is(bookings.get(1).getItem().getId()), Long.class))
               .andExpect(jsonPath("$[1].item.name", is(bookings.get(1).getItem().getName()), String.class));
    }

    private BookingDto getBookingDto(long id, LocalDateTime start, LocalDateTime end, BookingStatus status) {
        long itemId = 1;
        BookingDto.Item item = BookingDto.Item.builder()
                                              .withId(itemId)
                                              .withName("Test item")
                                              .build();

        long bookerId = 1;
        BookingDto.Booker booker = BookingDto.Booker.builder()
                                                    .withId(bookerId)
                                                    .build();
        return BookingDto.builder()
                         .withId(id)
                         .withStart(start)
                         .withEnd(end)
                         .withBooker(booker)
                         .withStatus(status)
                         .withItem(item)
                         .build();
    }
}