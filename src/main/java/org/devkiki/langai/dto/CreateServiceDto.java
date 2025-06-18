package org.devkiki.langai.dto;

public record CreateServiceDto(
        String name,
        String description,
        Double price
) {
}
