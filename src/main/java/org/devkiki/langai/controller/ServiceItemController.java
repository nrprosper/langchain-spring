package org.devkiki.langai.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.devkiki.langai.dto.CreateServiceDto;
import org.devkiki.langai.model.ServiceItem;
import org.devkiki.langai.service.ServiceItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
@Tag(name = "Services Controller", description = "Endpoints to manage Services in H2 database")
public class ServiceItemController {
    private final ServiceItemService service;

    @PostMapping
    @Operation(summary = "Create a new service")
    public ResponseEntity<ServiceItem> createServiceItem(@RequestBody CreateServiceDto dto) {
        var createdService = service.create(dto);
        return new ResponseEntity<>(createdService, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all services")
    public ResponseEntity<List<ServiceItem>> getAllServiceItems() {
        var services = service.all();
        return new ResponseEntity<>(services, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get one service")
    public ResponseEntity<ServiceItem> getServiceItem(@PathVariable Long id) {
        var serviceItem = service.find(id);
        return new ResponseEntity<>(serviceItem, HttpStatus.OK);
    }


}
