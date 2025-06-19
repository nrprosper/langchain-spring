package org.devkiki.langai;

import dev.langchain4j.agent.tool.Tool;
import lombok.RequiredArgsConstructor;
import org.devkiki.langai.model.Booking;
import org.devkiki.langai.model.ServiceItem;
import org.devkiki.langai.repository.BookingRepository;
import org.devkiki.langai.service.ServiceItemService;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BookingTool {
    private final ServiceItemService serviceItemService;
    private final BookingRepository  bookingRepository;


    @Tool("List all available services for booking")
    public List<ServiceItem> findAll() {
        return serviceItemService.all();
    }

    @Tool("Book a service for a user at a given time")
    public Booking bookService(Long serviceId, String fullName, String email, String time) {
        LocalDateTime bookingTime = LocalDateTime.parse(time);
        ServiceItem service = serviceItemService.find(serviceId);
        Booking booking = new Booking();
        booking.setFullName(fullName);
        booking.setEmail(email);
        booking.setService(service);
        booking.setBookingTime(bookingTime);
        return bookingRepository.save(booking);
    }

    @Tool("View all bookings for a given email")
    public List<Booking> getBookings(String email) {
        return bookingRepository.findByEmail(email);
    }

}
