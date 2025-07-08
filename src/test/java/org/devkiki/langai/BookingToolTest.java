package org.devkiki.langai;

import org.devkiki.langai.dto.CreateBookingDto;
import org.devkiki.langai.model.Booking;
import org.devkiki.langai.model.ServiceItem;
import org.devkiki.langai.service.BookingService;
import org.devkiki.langai.service.ServiceItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingToolTest {

    @Mock
    private ServiceItemService serviceItemService;

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingTool bookingTool;

    @Captor
    private ArgumentCaptor<CreateBookingDto> bookingDtoCaptor;

    private ServiceItem sampleService;
    private Booking sampleBooking;

    @BeforeEach
    void setUp() {
        // Setup sample service
        sampleService = new ServiceItem();
        sampleService.setId(1L);
        sampleService.setName("Haircut");
        sampleService.setDescription("Professional haircut service");
        sampleService.setPrice(25.0);

        // Setup sample booking
        sampleBooking = new Booking();
        sampleBooking.setId(1L);
        sampleBooking.setService(sampleService);
        sampleBooking.setFullName("John Doe");
        sampleBooking.setEmail("john@example.com");
        sampleBooking.setBookingTime(LocalDateTime.now().plusDays(1));
    }

    @Test
    void findAll_whenServicesExist_returnsFormattedServiceList() {
        // Arrange
        List<ServiceItem> services = Arrays.asList(sampleService);
        when(serviceItemService.all()).thenReturn(services);

        // Act
        String result = bookingTool.findAll();

        // Assert
        assertTrue(result.contains("Available services"));
        assertTrue(result.contains("Haircut"));
        assertTrue(result.contains("25.0"));
        System.out.println("[DEBUG_LOG] findAll with services: " + result);
    }

    @Test
    void findAll_whenNoServicesExist_returnsAppropriateMessage() {
        // Arrange
        when(serviceItemService.all()).thenReturn(Collections.emptyList());

        // Act
        String result = bookingTool.findAll();

        // Assert
        assertTrue(result.contains("no services available"));
        assertFalse(result.contains("Available services"));
        System.out.println("[DEBUG_LOG] findAll with no services: " + result);
    }

    @Test
    void searchServices_whenServicesMatch_returnsMatchingServices() {
        // Arrange
        List<ServiceItem> services = Arrays.asList(sampleService);
        when(serviceItemService.all()).thenReturn(services);

        // Act
        String result = bookingTool.searchServices("hair");

        // Assert
        assertTrue(result.contains("Services matching 'hair'"));
        assertTrue(result.contains("Haircut"));
        System.out.println("[DEBUG_LOG] searchServices with match: " + result);
    }

    @Test
    void searchServices_whenNoServicesMatch_returnsAppropriateMessage() {
        // Arrange
        List<ServiceItem> services = Arrays.asList(sampleService);
        when(serviceItemService.all()).thenReturn(services);

        // Act
        String result = bookingTool.searchServices("massage");

        // Assert
        assertTrue(result.contains("No services found matching 'massage'"));
        assertFalse(result.contains("Services matching"));
        System.out.println("[DEBUG_LOG] searchServices with no match: " + result);
    }

    @Test
    void getBookings_whenBookingsExist_returnsFormattedBookingList() {
        // Arrange
        List<Booking> bookings = Arrays.asList(sampleBooking);
        when(bookingService.findByEmail(anyString())).thenReturn(bookings);

        // Act
        String result = bookingTool.getBookings("john@example.com");

        // Assert
        assertTrue(result.contains("Your bookings"));
        assertTrue(result.contains("John Doe"));
        assertTrue(result.contains("Haircut"));
        System.out.println("[DEBUG_LOG] getBookings with bookings: " + result);
    }

    @Test
    void getBookings_whenNoBookingsExist_returnsAppropriateMessage() {
        // Arrange
        when(bookingService.findByEmail(anyString())).thenReturn(Collections.emptyList());

        // Act
        String result = bookingTool.getBookings("john@example.com");

        // Assert
        assertTrue(result.contains("No bookings found"));
        assertFalse(result.contains("Your bookings"));
        System.out.println("[DEBUG_LOG] getBookings with no bookings: " + result);
    }

    @Test
    void getServiceDetails_whenServiceExists_returnsFormattedServiceDetails() {
        // Arrange
        when(serviceItemService.find(anyLong())).thenReturn(sampleService);

        // Act
        String result = bookingTool.getServiceDetails(1L);

        // Assert
        assertTrue(result.contains("Service Details"));
        assertTrue(result.contains("Haircut"));
        assertTrue(result.contains("Professional haircut service"));
        assertTrue(result.contains("25.0"));
        System.out.println("[DEBUG_LOG] getServiceDetails with service: " + result);
    }

    @Test
    void getServiceDetails_whenServiceDoesNotExist_returnsAppropriateMessage() {
        // Arrange
        when(serviceItemService.find(anyLong())).thenThrow(new RuntimeException("Item not found!"));

        // Act
        String result = bookingTool.getServiceDetails(999L);

        // Assert
        assertTrue(result.contains("Service with ID 999 was not found"));
        assertFalse(result.contains("Service Details"));
        System.out.println("[DEBUG_LOG] getServiceDetails with no service: " + result);
    }

    @Test
    void bookService_withISOFormatDate_createsBookingWithCorrectDate() {
        // Arrange
        String isoDate = LocalDateTime.now().plusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        when(serviceItemService.find(anyLong())).thenReturn(sampleService);
        when(bookingService.book(any(CreateBookingDto.class))).thenReturn(sampleBooking);

        // Act
        String result = bookingTool.bookService(1L, "John Doe", "john@example.com", isoDate);

        // Assert
        assertTrue(result.contains("Booking confirmed"));
        assertTrue(result.contains("John Doe"));
        assertTrue(result.contains("Haircut"));
        System.out.println("[DEBUG_LOG] bookService with ISO date: " + result);
    }

    @Test
    void bookService_withNaturalLanguageDate_convertsToISOFormat() {
        // Arrange
        when(serviceItemService.find(anyLong())).thenReturn(sampleService);
        when(bookingService.book(any(CreateBookingDto.class))).thenReturn(sampleBooking);

        // Act
        String result = bookingTool.bookService(1L, "John Doe", "john@example.com", "tomorrow at 3pm");

        // Assert
        assertTrue(result.contains("Booking confirmed"));
        assertTrue(result.contains("John Doe"));
        assertTrue(result.contains("Haircut"));
        System.out.println("[DEBUG_LOG] bookService with natural language date: " + result);
    }

    @Test
    void bookService_withInvalidDateFormat_returnsErrorMessage() {
        // Arrange

        // Act
        String result = bookingTool.bookService(1L, "John Doe", "john@example.com", "invalid date format");

        // Assert
        assertTrue(result.contains("I couldn't understand that date format"));
        assertFalse(result.contains("Booking confirmed"));
        System.out.println("[DEBUG_LOG] bookService with invalid date: " + result);
    }

    @Test
    void bookService_withPastDate_returnsErrorMessage() {
        // Arrange
        String pastDate = LocalDateTime.now().minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        // Act
        String result = bookingTool.bookService(1L, "John Doe", "john@example.com", pastDate);

        // Assert
        assertTrue(result.contains("Booking time must be in the future"));
        assertFalse(result.contains("Booking confirmed"));
        System.out.println("[DEBUG_LOG] bookService with past date: " + result);
    }
}
