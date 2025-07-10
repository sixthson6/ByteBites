package com.tech.restaurant_service.controller;

import com.tech.restaurant_service.dto.ProductRequest;
import com.tech.restaurant_service.dto.ProductResponse;
import com.tech.restaurant_service.dto.RestaurantRequest;
import com.tech.restaurant_service.dto.RestaurantResponse;
import com.tech.restaurant_service.service.RestaurantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/restaurants")
public class RestaurantController {

    private static final Logger logger = LoggerFactory.getLogger(RestaurantController.class);

    private final RestaurantService restaurantService;

    @Autowired
    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @PostMapping
    public ResponseEntity<RestaurantResponse> createRestaurant(@Valid @RequestBody RestaurantRequest restaurantRequest) {
        logger.info("Received request to create restaurant: {}", restaurantRequest.getName());
        RestaurantResponse response = restaurantService.createRestaurant(restaurantRequest);
        logger.info("Restaurant created with ID: {}", response.getId());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<RestaurantResponse>> getAllRestaurants() {
        logger.info("Received request to get all restaurants");
        List<RestaurantResponse> response = restaurantService.getAllRestaurants();
        logger.info("Returning {} restaurants", response.size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestaurantResponse> getRestaurantById(@PathVariable Long id) {
        logger.info("Received request to get restaurant by ID: {}", id);
        return restaurantService.getRestaurantById(id)
                .map(response -> {
                    logger.info("Returning restaurant with ID: {}", response.getId());
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    logger.warn("Restaurant with ID {} not found", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @PutMapping("/{id}")
    public ResponseEntity<RestaurantResponse> updateRestaurant(@PathVariable Long id, @Valid @RequestBody RestaurantRequest restaurantRequest) {
        logger.info("Received request to update restaurant with ID {}: {}", id, restaurantRequest.getName());
        RestaurantResponse response = restaurantService.updateRestaurant(id, restaurantRequest);
        logger.info("Restaurant with ID {} updated", response.getId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRestaurant(@PathVariable Long id) {
        logger.info("Received request to delete restaurant with ID: {}", id);
        restaurantService.deleteRestaurant(id);
        logger.info("Restaurant with ID {} deleted", id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{restaurantId}/menu")
    public ResponseEntity<ProductResponse> addMenuItem(@PathVariable Long restaurantId, @Valid @RequestBody ProductRequest productRequest) {
        logger.info("Received request to add menu item '{}' to restaurant ID: {}", productRequest.getName(), restaurantId);
        ProductResponse response = restaurantService.addMenuItem(restaurantId, productRequest);
        logger.info("Menu item '{}' added to restaurant ID {}", response.getName(), restaurantId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{restaurantId}/menu/{menuItemId}")
    public ResponseEntity<ProductResponse> updateMenuItem(@PathVariable Long restaurantId, @PathVariable Long menuItemId, @Valid @RequestBody ProductRequest productRequest) {
        logger.info("Received request to update menu item ID {} for restaurant ID {}: {}", menuItemId, restaurantId, productRequest.getName());
        ProductResponse response = restaurantService.updateMenuItem(restaurantId, menuItemId, productRequest);
        logger.info("Menu item ID {} for restaurant ID {} updated", menuItemId, restaurantId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{restaurantId}/menu/{menuItemId}")
    public ResponseEntity<Void> deleteMenuItem(@PathVariable Long restaurantId, @PathVariable Long menuItemId) {
        logger.info("Received request to delete menu item ID {} from restaurant ID: {}", menuItemId, restaurantId);
        restaurantService.deleteMenuItem(restaurantId, menuItemId);
        logger.info("Menu item ID {} from restaurant ID {} deleted", menuItemId, restaurantId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{restaurantId}/menu")
    public ResponseEntity<List<ProductResponse>> getMenuForRestaurant(@PathVariable Long restaurantId) {
        logger.info("Received request to get menu for restaurant ID: {}", restaurantId);
        List<ProductResponse> response = restaurantService.getMenuForRestaurant(restaurantId);
        logger.info("Returning {} menu items for restaurant ID {}", response.size(), restaurantId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{restaurantId}/menu/{menuItemId}")
    public ResponseEntity<ProductResponse> getMenuItemById(@PathVariable Long restaurantId, @PathVariable Long menuItemId) {
        logger.info("Received request to get menu item ID {} for restaurant ID: {}", menuItemId, restaurantId);
        return restaurantService.getMenuItemById(restaurantId, menuItemId)
                .map(response -> {
                    logger.info("Returning menu item ID {} for restaurant ID {}", response.getId(), restaurantId);
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    logger.warn("Menu item ID {} for restaurant ID {} not found", menuItemId, restaurantId);
                    return ResponseEntity.notFound().build();
                });
    }

    @GetMapping("/menu")
    public ResponseEntity<List<ProductResponse>> getAllMenuItems() {
        logger.info("Received request to get all menu items");
        List<ProductResponse> response = restaurantService.getAllMenuItems();
        logger.info("Returning {} menu items", response.size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/menu/search")
    public ResponseEntity<List<ProductResponse>> searchMenuItemsByName(@RequestParam String name) {
        logger.info("Received request to search menu items by name: {}", name);
        List<ProductResponse> response = restaurantService.searchMenuItemsByName(name);
        logger.info("Returning {} menu items matching name '{}'", response.size(), name);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<List<RestaurantResponse>> searchRestaurants(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String menuItem) {

        logger.info("Received request to search restaurants with name: {}, city: {}, menuItem: {}", name, city, menuItem);

        if (name != null && city != null && menuItem != null) {
            // This combination is not directly supported by a single service method
            // You might need to implement a more complex search logic in the service layer
            // For now, returning an empty list or throwing an error
            List<RestaurantResponse> response = restaurantService.searchRestaurantsByName(name); // Fallback
            logger.info("Returning {} restaurants matching name '{}', city '{}', menuItem '{}' (fallback to name search)", response.size(), name, city, menuItem);
            return ResponseEntity.ok(response);
        } else if (name != null && city != null) {
            // Search by name and city
            List<RestaurantResponse> response = restaurantService.searchRestaurantsByName(name); // Needs refinement
            logger.info("Returning {} restaurants matching name '{}' and city '{}' (fallback to name search)", response.size(), name, city);
            return ResponseEntity.ok(response);
        } else if (name != null && menuItem != null) {
            // Search by name and menu item
            List<RestaurantResponse> response = restaurantService.searchRestaurantsByName(name); // Needs refinement
            logger.info("Returning {} restaurants matching name '{}' and menu item '{}' (fallback to name search)", response.size(), name, menuItem);
            return ResponseEntity.ok(response);
        } else if (city != null && menuItem != null) {
            // Search by city and menu item
            List<RestaurantResponse> response = restaurantService.searchRestaurantsByCity(city); // Needs refinement
            logger.info("Returning {} restaurants matching city '{}' and menu item '{}' (fallback to city search)", response.size(), city, menuItem);
            return ResponseEntity.ok(response);
        } else if (name != null) {
            List<RestaurantResponse> response = restaurantService.searchRestaurantsByName(name);
            logger.info("Returning {} restaurants matching name '{}'", response.size(), name);
            return ResponseEntity.ok(response);
        } else if (city != null) {
            List<RestaurantResponse> response = restaurantService.searchRestaurantsByCity(city);
            logger.info("Returning {} restaurants matching city '{}'", response.size(), city);
            return ResponseEntity.ok(response);
        } else if (menuItem != null) {
            List<RestaurantResponse> response = restaurantService.searchRestaurantsByMenuItem(menuItem);
            logger.info("Returning {} restaurants matching menu item '{}'", response.size(), menuItem);
            return ResponseEntity.ok(response);
        } else {
            List<RestaurantResponse> response = restaurantService.getAllRestaurants();
            logger.info("Returning all {} restaurants", response.size());
            return ResponseEntity.ok(response);
        }
    }
}