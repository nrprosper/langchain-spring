package org.devkiki.langai;

import dev.langchain4j.agent.tool.Tool;
import lombok.RequiredArgsConstructor;
import org.devkiki.langai.dto.CreateBookingDto;
import org.devkiki.langai.model.Booking;
import org.devkiki.langai.model.ServiceItem;
import org.devkiki.langai.service.BookingService;
import org.devkiki.langai.service.ServiceItemService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BookingTool {
    private final ServiceItemService serviceItemService;
    private final BookingService  bookingService;


    @Tool("List all available services for booking")
    public List<ServiceItem> findAll() {
        return serviceItemService.all();
    }

    @Tool("Book a service for a user. Time must be in ISO-8601 format (e.g. 2025-06-20T12:00:00)")
    public Booking bookService(Long serviceId, String fullName, String email, String time) {
        var dto = new CreateBookingDto(serviceId, fullName, email, time);
        return bookingService.book(dto);
    }

    @Tool("View all bookings for a given email")
    public List<Booking> getBookings(String email) {
        return bookingService.findByEmail(email);
    }

}
