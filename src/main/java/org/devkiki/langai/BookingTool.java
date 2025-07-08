package org.devkiki.langai;

import dev.langchain4j.agent.tool.Tool;
import lombok.RequiredArgsConstructor;
import org.devkiki.langai.dto.CreateBookingDto;
import org.devkiki.langai.model.Booking;
import org.devkiki.langai.model.ServiceItem;
import org.devkiki.langai.service.BookingService;
import org.devkiki.langai.service.ServiceItemService;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class BookingTool {
    private final ServiceItemService serviceItemService;
    private final BookingService bookingService;

    @Tool("Respond to identity and developer questions")
    public String whoIsYourDeveloper() {
        return "I was developed by Nishimwe Prosper as a personal side project.";
    }

    @Tool("Reject any off-topic or unrelated requests politely")
    public String rejectOffTopic() {
        return "I'm only able to help with booking-related tasks right now.";
    }

    @Tool("List all available services for booking")
    public String findAll() {
        List<ServiceItem> services = serviceItemService.all();
        if (services.isEmpty()) {
            return "There are currently no services available for booking. Please check back later.";
        }

        StringBuilder result = new StringBuilder("Available services:\n");
        for (ServiceItem service : services) {
            result.append(", Name: ").append(service.getName())
                  .append(", Price: $").append(service.getPrice())
                  .append("\n");
        }
        return result.toString();
    }

    @Tool("Search for services by name or description")
    public String searchServices(String query) {
        if (query == null || query.trim().isEmpty()) {
            return "Please provide a search term to find services.";
        }

        List<ServiceItem> allServices = serviceItemService.all();
        if (allServices.isEmpty()) {
            return "There are currently no services available in our system.";
        }

        String lowercaseQuery = query.toLowerCase();
        List<ServiceItem> matchingServices = allServices.stream()
            .filter(service -> 
                (service.getName() != null && service.getName().toLowerCase().contains(lowercaseQuery)) ||
                (service.getDescription() != null && service.getDescription().toLowerCase().contains(lowercaseQuery)))
            .toList();

        if (matchingServices.isEmpty()) {
            return "No services found matching '" + query + "'. Try a different search term or view all available services.";
        }

        StringBuilder result = new StringBuilder("Services matching '" + query + "':\n");
        for (ServiceItem service : matchingServices) {
            result.append("Name: ").append(service.getName())
                  .append(", Price: $").append(service.getPrice())
                  .append("\n");
        }
        return result.toString();
    }

    @Tool("Get detailed information about a specific service by ID")
    public String getServiceDetails(Long serviceId) {
        if (serviceId == null) {
            return "Please provide a valid service ID.";
        }

        try {
            ServiceItem service = serviceItemService.find(serviceId);
            return String.format(
                "Service Details:\n" +
                "- Name: %s\n" +
                "- Description: %s\n" +
                "- Price: $%.2f",
                service.getId(), service.getName(), service.getDescription(), service.getPrice()
            );
        } catch (RuntimeException e) {
            return "Service with ID " + serviceId + " was not found. Please check the ID and try again.";
        }
    }

    private String parseNaturalLanguageDate(String naturalLanguageDate) {
        if (naturalLanguageDate == null || naturalLanguageDate.trim().isEmpty()) {
            return null;
        }

        String input = naturalLanguageDate.toLowerCase().trim();
        LocalDate date = null;
        LocalTime time = null;

        try {
            return LocalDateTime.parse(input).toString();
        } catch (DateTimeParseException e) {
            // Continue with natural language parsing
        }

        if (input.contains("today")) {
            date = LocalDate.now();
        } else if (input.contains("tomorrow")) {
            date = LocalDate.now().plusDays(1);
        } else if (input.contains("next week")) {
            date = LocalDate.now().plusWeeks(1);
        } else if (input.matches(".*next\\s+monday.*")) {
            date = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        } else if (input.matches(".*next\\s+tuesday.*")) {
            date = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.TUESDAY));
        } else if (input.matches(".*next\\s+wednesday.*")) {
            date = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.WEDNESDAY));
        } else if (input.matches(".*next\\s+thursday.*")) {
            date = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.THURSDAY));
        } else if (input.matches(".*next\\s+friday.*")) {
            date = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.FRIDAY));
        } else if (input.matches(".*next\\s+saturday.*")) {
            date = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.SATURDAY));
        } else if (input.matches(".*next\\s+sunday.*")) {
            date = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.SUNDAY));
        }

        Pattern timePattern = Pattern.compile("(\\d{1,2})(?::(\\d{2}))?\\s*(am|pm)?");
        Matcher matcher = timePattern.matcher(input);

        if (matcher.find()) {
            int hour = Integer.parseInt(matcher.group(1));
            int minute = matcher.group(2) != null ? Integer.parseInt(matcher.group(2)) : 0;
            String ampm = matcher.group(3);

            if (ampm != null && ampm.equals("pm") && hour < 12) {
                hour += 12;
            } else if (ampm != null && ampm.equals("am") && hour == 12) {
                hour = 0;
            }

            time = LocalTime.of(hour, minute);
        } else if (input.contains("noon")) {
            time = LocalTime.of(12, 0);
        } else if (input.contains("midnight")) {
            time = LocalTime.of(0, 0);
        } else if (input.contains("morning")) {
            time = LocalTime.of(9, 0);
        } else if (input.contains("afternoon")) {
            time = LocalTime.of(14, 0);
        } else if (input.contains("evening")) {
            time = LocalTime.of(18, 0);
        } else if (input.contains("night")) {
            time = LocalTime.of(20, 0);
        }

        if (date == null || time == null) {
            return null;
        }

        return LocalDateTime.of(date, time).toString();
    }

    @Tool("Book a service for a user. You can use natural language dates (e.g., 'tomorrow at 3pm') or ISO-8601 format (e.g. 2025-06-20T12:00:00)")
    public String bookService(Long serviceId, String fullName, String email, String time) {
        if (serviceId == null) {
            return "Please provide a valid service ID.";
        }
        if (fullName == null || fullName.trim().isEmpty()) {
            return "Please provide your full name.";
        }
        if (email == null || email.trim().isEmpty() || !email.contains("@")) {
            return "Please provide a valid email address.";
        }
        if (time == null || time.trim().isEmpty()) {
            return "Please provide a booking time. You can use natural language (e.g., 'tomorrow at 3pm') or ISO-8601 format (e.g., '2025-06-20T12:00:00').";
        }

        // Try to parse as natural language first, then fall back to ISO format
        String isoDateTime = parseNaturalLanguageDate(time);

        LocalDateTime dateTime;
        try {
            // If natural language parsing failed, try direct ISO parsing
            if (isoDateTime == null) {
                dateTime = LocalDateTime.parse(time);
                // Convert to ISO format for consistency
                isoDateTime = dateTime.toString();
            } else {
                dateTime = LocalDateTime.parse(isoDateTime);
            }

            if (dateTime.isBefore(LocalDateTime.now())) {
                return "Booking time must be in the future. Please select a future date and time.";
            }
        } catch (DateTimeParseException e) {
            return "I couldn't understand that date format. Please use natural language (e.g., 'tomorrow at 3pm') or ISO-8601 format (e.g., '2025-06-20T12:00:00').";
        }

        try {
            // Use the ISO-formatted date string instead of the original time string
            var dto = new CreateBookingDto(serviceId, fullName, email, isoDateTime);
            Booking booking = bookingService.book(dto);

            ServiceItem service = booking.getService();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            return String.format(
                "Booking confirmed!\n" +
                "- Service: %s\n" +
                "- Price: $%.2f\n" +
                "- Date and Time: %s\n" +
                "- Booking ID: %d\n" +
                "- Name: %s\n" +
                "- Email: %s\n\n" +
                "Thank you for your booking!",
                service.getName(),
                service.getPrice(),
                booking.getBookingTime().format(formatter),
                booking.getId(),
                booking.getFullName(),
                booking.getEmail()
            );
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Service not found")) {
                return "Service with ID " + serviceId + " was not found. Please check the ID and try again.";
            } else {
                return "There was an error processing your booking: " + e.getMessage() + 
                       ". Please check your information and try again.";
            }
        }
    }

    @Tool("View all bookings for a given email")
    public String getBookings(String email) {
        if (email == null || email.trim().isEmpty() || !email.contains("@")) {
            return "Please provide a valid email address.";
        }

        List<Booking> bookings = bookingService.findByEmail(email);
        if (bookings.isEmpty()) {
            return "No bookings found for email: " + email + ". Make sure you've used the correct email address.";
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        StringBuilder result = new StringBuilder("Your bookings:\n");

        for (Booking booking : bookings) {
            ServiceItem service = booking.getService();
            result.append("- Booking ID: ").append(booking.getId())
                  .append(", Name: ").append(booking.getFullName())
                  .append(", Service: ").append(service.getName())
                  .append(", Date: ").append(booking.getBookingTime().format(formatter))
                  .append(", Price: $").append(service.getPrice())
                  .append("\n");
        }

        return result.toString();
    }

    @Tool("Cancel a booking by ID and email (for verification)")
    public String cancelBooking(Long bookingId, String email) {
        if (bookingId == null) {
            return "Please provide a valid booking ID.";
        }
        if (email == null || email.trim().isEmpty() || !email.contains("@")) {
            return "Please provide a valid email address for verification.";
        }

        List<Booking> userBookings = bookingService.findByEmail(email);
        Optional<Booking> bookingToCancel = userBookings.stream()
            .filter(b -> b.getId().equals(bookingId))
            .findFirst();

        if (bookingToCancel.isEmpty()) {
            return "No booking found with ID " + bookingId + " for email " + email + 
                   ". Please check your booking ID and email address.";
        }

        return "Booking with ID " + bookingId + " has been successfully cancelled.";
    }

    @Tool("Check availability for a service on a specific date")
    public String checkAvailability(Long serviceId, String date) {
        if (serviceId == null) {
            return "Please provide a valid service ID.";
        }
        if (date == null || date.trim().isEmpty()) {
            return "Please provide a date in ISO format (e.g., 2025-06-20).";
        }

        try {
            ServiceItem service = serviceItemService.find(serviceId);

            return String.format(
                "Available time slots for %s on %s:\n" +
                "- 09:00 AM\n" +
                "- 11:30 AM\n" +
                "- 02:00 PM\n" +
                "- 04:30 PM\n\n" +
                "To book, use the booking tool with your preferred time in ISO-8601 format (e.g., %sT09:00:00).",
                service.getName(), date, date
            );
        } catch (RuntimeException e) {
            return "Service with ID " + serviceId + " was not found. Please check the ID and try again.";
        }
    }
}
