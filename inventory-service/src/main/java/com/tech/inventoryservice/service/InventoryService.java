package com.tech.inventoryservice.service;

import com.tech.inventoryservice.model.Inventory;

import java.util.List;
import java.util.Optional;

public interface InventoryService {
    Inventory saveInventory(Inventory inventory);
    Optional<Inventory> findById(Long id);
    List<Inventory> findAll();
    void deleteById(Long id);
}