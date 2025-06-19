package org.devkiki.langai.dto;

public record CreateBookingDto(
        Long serviceId,
        String fullName,
        String email,
        String time
) {
}
