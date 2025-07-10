package com.tech.restaurant_service.service;

import com.tech.restaurant_service.dto.ProductRequest;
import com.tech.restaurant_service.dto.ProductResponse;
import com.tech.restaurant_service.dto.RestaurantRequest;
import com.tech.restaurant_service.dto.RestaurantResponse;
import com.tech.restaurant_service.entity.Restaurant;

import java.util.List;
import java.util.Optional;

public interface RestaurantService {
    List<RestaurantResponse> getAllRestaurants();
    Optional<RestaurantResponse> getRestaurantById(Long id);
    RestaurantResponse createRestaurant(RestaurantRequest restaurantRequest);
    RestaurantResponse updateRestaurant(Long id, RestaurantRequest updatedRestaurantRequest);
    void deleteRestaurant(Long id);
    ProductResponse addMenuItem(Long restaurantId, ProductRequest productRequest);
    ProductResponse updateMenuItem(Long restaurantId, Long menuItemId, ProductRequest productRequest);
    void deleteMenuItem(Long restaurantId, Long menuItemId);
    List<ProductResponse> getMenuForRestaurant(Long restaurantId);
    Optional<ProductResponse> getMenuItemById(Long restaurantId, Long menuItemId);
    List<ProductResponse> getAllMenuItems();
    List<ProductResponse> searchMenuItemsByName(String name);
    List<RestaurantResponse> searchRestaurantsByMenuItem(String menuItemName);
    List<RestaurantResponse> searchRestaurantsByCity(String city);
    List<RestaurantResponse> searchRestaurantsByName(String name);
}