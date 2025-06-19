package org.devkiki.langai.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.devkiki.langai.dto.CreateBookingDto;
import org.devkiki.langai.model.Booking;
import org.devkiki.langai.service.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@Tag(name = "Bookings Controller", description = "Endpoints to manage bookings")
public class BookingController {
    private final BookingService service;

    @PostMapping
    @Operation(summary = "Booking endpoint")
    public ResponseEntity<Booking> addBooking(@RequestBody CreateBookingDto dto) {
        var booked = service.book(dto);
        return new ResponseEntity<>(booked, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all bookings")
    public ResponseEntity<List<Booking>> getBookings() {
        var allBooked = service.all();
        return new ResponseEntity<>(allBooked, HttpStatus.OK);
    }

    @GetMapping("/{email}")
    @Operation(summary = "Get all booked by user - Just add in email")
    public ResponseEntity<List<Booking>> getBooking(@PathVariable String email) {
        var allBooked = service.findByEmail(email);
        return new ResponseEntity<>(allBooked, HttpStatus.OK);
    }


}
