package com.tech.inventoryservice.service;

import com.tech.inventoryservice.model.Inventory;
import com.tech.inventoryservice.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    @Transactional(readOnly = true)
    public boolean isInStock(String skuCode, Integer quantity) {
        Optional<Inventory> inventoryOptional = inventoryRepository.findBySkuCode(skuCode);
        return inventoryOptional.isPresent() && inventoryOptional.get().getQuantity() >= quantity;
    }

    @Transactional
    public void decrementStock(String skuCode, Integer quantity) {
        Inventory inventory = inventoryRepository.findBySkuCode(skuCode)
                .orElseThrow(() -> new RuntimeException("Inventory not found for skuCode: " + skuCode));
        if (inventory.getQuantity() < quantity) {
            throw new RuntimeException("Insufficient stock for skuCode: " + skuCode);
        }
        inventory.setQuantity(inventory.getQuantity() - quantity);
        inventoryRepository.save(inventory);
    }
}
