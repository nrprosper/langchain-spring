package org.devkiki.langai.controller;

import org.devkiki.langai.dto.CreateServiceDto;
import org.devkiki.langai.model.ServiceItem;
import org.devkiki.langai.service.ServiceItemService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/services")
public class ServiceItemController {
    private final ServiceItemService service;

    public ServiceItemController(ServiceItemService service) {
        this.service = service;
    }

    @PostMapping
    public ServiceItem createServiceItem(@RequestBody CreateServiceDto dto) {
        return service.create(dto);
    }


}
