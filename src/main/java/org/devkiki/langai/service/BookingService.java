package org.devkiki.langai.service;

import lombok.RequiredArgsConstructor;
import org.devkiki.langai.dto.CreateBookingDto;
import org.devkiki.langai.model.Booking;
import org.devkiki.langai.model.ServiceItem;
import org.devkiki.langai.repository.BookingRepository;
import org.devkiki.langai.repository.ServiceItemRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository repository;
    private final ServiceItemRepository sRepository;

    public Booking book(CreateBookingDto dto) {
        ServiceItem foundService = sRepository.findById(dto.serviceId()).orElseThrow(() -> new RuntimeException("Service not found"));
        LocalDateTime dateTime = LocalDateTime.parse(dto.time());
        Booking booking = new Booking();
        booking.setFullName(dto.fullName());
        booking.setEmail(dto.email());
        booking.setService(foundService);
        booking.setBookingTime(dateTime);

        return  repository.save(booking);
    }

    public List<Booking> all() {
        return repository.findAll();
    }

    public List<Booking> findByEmail(String email) {
        return repository.findByEmail(email);
    }

}
