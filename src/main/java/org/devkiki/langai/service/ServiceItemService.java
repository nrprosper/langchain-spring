package org.devkiki.langai.service;

import org.devkiki.langai.dto.CreateServiceDto;
import org.devkiki.langai.model.ServiceItem;
import org.devkiki.langai.repository.ServiceItemRepository;
import org.springframework.stereotype.Service;

@Service
public class ServiceItemService {
    private final ServiceItemRepository repository;

    public ServiceItemService(final ServiceItemRepository repository) {
        this.repository = repository;
    }

    public ServiceItem create(CreateServiceDto dto) {
        ServiceItem serviceItem = new ServiceItem();
        serviceItem.setName(dto.name());
        serviceItem.setDescription(dto.description());
        serviceItem.setPrice(dto.price());

        return repository.save(serviceItem);
    }


}
