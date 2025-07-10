package com.tech.restaurant_service.repository;

import com.tech.restaurant_service.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    List<Restaurant> findByAddressContainingIgnoreCase(String city);
    List<Restaurant> findByMenuNameContainingIgnoreCase(String menuName);
    List<Restaurant> findByNameContainingIgnoreCase(String name);
}