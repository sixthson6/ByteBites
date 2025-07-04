package com.tech.inventoryservice.service;

import com.tech.inventoryservice.model.Inventory;
import com.tech.inventoryservice.repository.InventoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;

    public InventoryServiceImpl(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    public Inventory saveInventory(Inventory inventory) {
        return inventoryRepository.save(inventory);
    }

    @Override
    public Optional<Inventory> findById(Long id) {
        return inventoryRepository.findById(id);
    }

    @Override
    public List<Inventory> findAll() {
        return inventoryRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        inventoryRepository.deleteById(id);
    }
}
