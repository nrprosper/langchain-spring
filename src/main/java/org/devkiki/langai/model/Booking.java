package org.devkiki.langai.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private ServiceItem service;
    @Column(unique = true, nullable = false, name = "full_name")
    private String fullName;
    private String email;
    @Column(name = "booking_time")
    private LocalDateTime bookingTime;
}
