package com.tech.inventoryservice.controller;

import com.tech.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/{skuCode}")
    @ResponseStatus(HttpStatus.OK)
    public boolean isInStock(@PathVariable String skuCode, @RequestParam Integer quantity) {
        return inventoryService.isInStock(skuCode, quantity);
    }

    @PutMapping("/{skuCode}/decrement")
    @ResponseStatus(HttpStatus.OK)
    public void decrementStock(@PathVariable String skuCode, @RequestParam Integer quantity) {
        inventoryService.decrementStock(skuCode, quantity);
    }
}
