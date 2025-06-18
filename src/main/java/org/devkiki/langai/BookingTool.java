package org.devkiki.langai;

import dev.langchain4j.agent.tool.Tool;
import org.devkiki.langai.model.Booking;
import org.devkiki.langai.model.ServiceItem;
import org.devkiki.langai.repository.BookingRepository;
import org.devkiki.langai.repository.ServiceItemRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class BookingTool {
    private final ServiceItemRepository serviceItemRepository;
    private final BookingRepository  bookingRepository;

    public BookingTool(ServiceItemRepository serviceItemRepository, BookingRepository bookingRepository) {
        this.serviceItemRepository = serviceItemRepository;
        this.bookingRepository = bookingRepository;
    }

    @Tool("List all available services for booking")
    public List<ServiceItem> findAll() {
        return serviceItemRepository.findAll();
    }

    @Tool("Book a service for a user at a given time")
    public Booking bookService(Long serviceId, String fullName, String email, String time) {
        LocalDateTime bookingTime = LocalDateTime.parse(time);
        ServiceItem service = serviceItemRepository.findById(serviceId).orElseThrow();
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
