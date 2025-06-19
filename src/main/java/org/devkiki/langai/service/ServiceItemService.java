package org.devkiki.langai.service;

import lombok.RequiredArgsConstructor;
import org.devkiki.langai.dto.CreateServiceDto;
import org.devkiki.langai.model.ServiceItem;
import org.devkiki.langai.repository.ServiceItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceItemService {
    private final ServiceItemRepository repository;

    public ServiceItem create(CreateServiceDto dto) {
        ServiceItem serviceItem = new ServiceItem();
        serviceItem.setName(dto.name());
        serviceItem.setDescription(dto.description());
        serviceItem.setPrice(dto.price());

        return repository.save(serviceItem);
    }


    public List<ServiceItem> all () {
        return repository.findAll();
    }

    public ServiceItem find (Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Item not found!"));
    }


}
