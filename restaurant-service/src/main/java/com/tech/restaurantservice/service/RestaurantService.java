package com.tech.restaurantservice.service;

import com.tech.restaurantservice.model.Restaurant;

import java.util.List;
import java.util.Optional;

public interface RestaurantService {
    Restaurant saveRestaurant(Restaurant restaurant);
    Optional<Restaurant> findById(Long id);
    List<Restaurant> findAll();
    void deleteById(Long id);
}