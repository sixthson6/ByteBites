package com.tech.restaurant_service.service;

import com.tech.restaurant_service.dto.ProductRequest;
import com.tech.restaurant_service.dto.ProductResponse;
import com.tech.restaurant_service.dto.RestaurantRequest;
import com.tech.restaurant_service.dto.RestaurantResponse;
import com.tech.restaurant_service.entity.Product;
import com.tech.restaurant_service.entity.Restaurant;
import com.tech.restaurant_service.exception.AccessDeniedException;
import com.tech.restaurant_service.exception.ResourceNotFoundException;
import com.tech.restaurant_service.repository.ProductRepository;
import com.tech.restaurant_service.repository.RestaurantRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RestaurantServiceImpl implements RestaurantService {

    private static final Logger logger = LoggerFactory.getLogger(RestaurantServiceImpl.class);

    private final RestaurantRepository restaurantRepository;
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public RestaurantServiceImpl(RestaurantRepository restaurantRepository, ProductRepository productRepository, ModelMapper modelMapper) {
        this.restaurantRepository = restaurantRepository;
        this.productRepository = productRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<RestaurantResponse> getAllRestaurants() {
        logger.info("Fetching all restaurants");
        List<RestaurantResponse> restaurants = restaurantRepository.findAll().stream()
                .map(restaurant -> modelMapper.map(restaurant, RestaurantResponse.class))
                .collect(Collectors.toList());
        logger.info("Fetched {} restaurants", restaurants.size());
        return restaurants;
    }

    @Override
    public Optional<RestaurantResponse> getRestaurantById(Long id) {
        logger.info("Fetching restaurant with ID: {}", id);
        Optional<RestaurantResponse> restaurant = restaurantRepository.findById(id)
                .map(r -> modelMapper.map(r, RestaurantResponse.class));
        restaurant.ifPresentOrElse(
                r -> logger.info("Found restaurant with ID: {}", id),
                () -> logger.warn("Restaurant with ID {} not found", id)
        );
        return restaurant;
    }

    @Override
    public RestaurantResponse createRestaurant(RestaurantRequest restaurantRequest) {
        logger.info("Creating new restaurant: {}", restaurantRequest.getName());
        Restaurant restaurant = modelMapper.map(restaurantRequest, Restaurant.class);
        if (restaurantRequest.getMenu() != null) {
            restaurantRequest.getMenu().forEach(productRequest -> {
                Product product = modelMapper.map(productRequest, Product.class);
                product.setRestaurant(restaurant);
                restaurant.getMenu().add(product);
            });
        }
        Restaurant savedRestaurant = restaurantRepository.save(restaurant);
        logger.info("Restaurant created with ID: {}", savedRestaurant.getId());
        return modelMapper.map(savedRestaurant, RestaurantResponse.class);
    }

    @Override
    public RestaurantResponse updateRestaurant(Long id, RestaurantRequest updatedRestaurantRequest) {
        logger.info("Updating restaurant with ID: {}", id);
        return restaurantRepository.findById(id)
                .map(restaurant -> {
                    restaurant.setName(updatedRestaurantRequest.getName());
                    restaurant.setAddress(updatedRestaurantRequest.getAddress());
                    Restaurant updated = restaurantRepository.save(restaurant);
                    logger.info("Restaurant with ID {} updated successfully", id);
                    return modelMapper.map(updated, RestaurantResponse.class);
                })
                .orElseThrow(() -> {
                    logger.error("Failed to update: Restaurant with ID {} not found", id);
                    return new ResourceNotFoundException("Restaurant not found with id " + id);
                });
    }

    @Override
    public void deleteRestaurant(Long id) {
        logger.info("Deleting restaurant with ID: {}", id);
        restaurantRepository.deleteById(id);
        logger.info("Restaurant with ID {} deleted successfully", id);
    }

    @Override
    public ProductResponse addMenuItem(Long restaurantId, ProductRequest productRequest) {
        logger.info("Adding menu item '{}' to restaurant ID: {}", productRequest.getName(), restaurantId);
        return restaurantRepository.findById(restaurantId)
                .map(restaurant -> {
                    Product menuItem = modelMapper.map(productRequest, Product.class);
                    menuItem.setRestaurant(restaurant);
                    Product savedMenuItem = productRepository.save(menuItem);
                    logger.info("Menu item '{}' added to restaurant ID {}", productRequest.getName(), restaurantId);
                    return modelMapper.map(savedMenuItem, ProductResponse.class);
                })
                .orElseThrow(() -> {
                    logger.error("Failed to add menu item: Restaurant with ID {} not found", restaurantId);
                    return new ResourceNotFoundException("Restaurant not found with id " + restaurantId);
                });
    }

    @Override
    public ProductResponse updateMenuItem(Long restaurantId, Long menuItemId, ProductRequest productRequest) {
        logger.info("Updating menu item ID {} for restaurant ID {}: {}", menuItemId, restaurantId, productRequest.getName());
        return productRepository.findById(menuItemId)
                .map(menuItem -> {
                    if (!menuItem.getRestaurant().getId().equals(restaurantId)) {
                        logger.error("Access denied: Menu item ID {} does not belong to restaurant ID {}", menuItemId, restaurantId);
                        throw new AccessDeniedException("Menu item does not belong to this restaurant");
                    }
                    menuItem.setName(productRequest.getName());
                    menuItem.setDescription(productRequest.getDescription());
                    menuItem.setPrice(productRequest.getPrice());
                    Product updated = productRepository.save(menuItem);
                    logger.info("Menu item ID {} for restaurant ID {} updated successfully", menuItemId, restaurantId);
                    return modelMapper.map(updated, ProductResponse.class);
                })
                .orElseThrow(() -> {
                    logger.error("Failed to update menu item: Menu item ID {} not found", menuItemId);
                    return new ResourceNotFoundException("Menu item not found with id " + menuItemId);
                });
    }

    @Override
    public void deleteMenuItem(Long restaurantId, Long menuItemId) {
        logger.info("Deleting menu item ID {} from restaurant ID: {}", menuItemId, restaurantId);
        productRepository.findById(menuItemId)
                .ifPresentOrElse(menuItem -> {
                    if (!menuItem.getRestaurant().getId().equals(restaurantId)) {
                        logger.error("Access denied: Menu item ID {} does not belong to restaurant ID {}", menuItemId, restaurantId);
                        throw new AccessDeniedException("Menu item does not belong to this restaurant");
                    }
                    productRepository.delete(menuItem);
                    logger.info("Menu item ID {} from restaurant ID {} deleted successfully", menuItemId, restaurantId);
                }, () -> {
                    logger.error("Failed to delete menu item: Menu item ID {} not found", menuItemId);
                    throw new ResourceNotFoundException("Menu item not found with id " + menuItemId);
                });
    }

    @Override
    public List<ProductResponse> getMenuForRestaurant(Long restaurantId) {
        logger.info("Fetching menu for restaurant ID: {}", restaurantId);
        List<ProductResponse> menu = restaurantRepository.findById(restaurantId)
                .map(restaurant -> restaurant.getMenu().stream()
                        .map(product -> modelMapper.map(product, ProductResponse.class))
                        .collect(Collectors.toList()))
                .orElseThrow(() -> {
                    logger.error("Failed to fetch menu: Restaurant with ID {} not found", restaurantId);
                    return new ResourceNotFoundException("Restaurant not found with id " + restaurantId);
                });
        logger.info("Fetched {} menu items for restaurant ID {}", menu.size(), restaurantId);
        return menu;
    }

    @Override
    public Optional<ProductResponse> getMenuItemById(Long restaurantId, Long menuItemId) {
        logger.info("Fetching menu item ID {} for restaurant ID: {}", menuItemId, restaurantId);
        Optional<ProductResponse> menuItem = productRepository.findById(menuItemId)
                .filter(item -> item.getRestaurant().getId().equals(restaurantId))
                .map(item -> modelMapper.map(item, ProductResponse.class));
        menuItem.ifPresentOrElse(
                item -> logger.info("Found menu item ID {} for restaurant ID {}", menuItemId, restaurantId),
                () -> logger.warn("Menu item ID {} for restaurant ID {} not found", menuItemId, restaurantId)
        );
        return menuItem;
    }

    @Override
    public List<ProductResponse> getAllMenuItems() {
        logger.info("Fetching all menu items");
        List<ProductResponse> menuItems = productRepository.findAll().stream()
                .map(product -> modelMapper.map(product, ProductResponse.class))
                .collect(Collectors.toList());
        logger.info("Fetched {} menu items", menuItems.size());
        return menuItems;
    }

    @Override
    public List<ProductResponse> searchMenuItemsByName(String name) {
        logger.info("Searching menu items by name: {}", name);
        List<ProductResponse> menuItems = productRepository.findByNameContainingIgnoreCase(name).stream()
                .map(product -> modelMapper.map(product, ProductResponse.class))
                .collect(Collectors.toList());
        logger.info("Found {} menu items matching name '{}'", menuItems.size(), name);
        return menuItems;
    }

    @Override
    public List<RestaurantResponse> searchRestaurantsByMenuItem(String menuItemName) {
        logger.info("Searching restaurants by menu item: {}", menuItemName);
        List<RestaurantResponse> restaurants = restaurantRepository.findByMenuNameContainingIgnoreCase(menuItemName).stream()
                .map(restaurant -> modelMapper.map(restaurant, RestaurantResponse.class))
                .collect(Collectors.toList());
        logger.info("Found {} restaurants with menu item '{}'", restaurants.size(), menuItemName);
        return restaurants;
    }

    @Override
    public List<RestaurantResponse> searchRestaurantsByCity(String city) {
        logger.info("Searching restaurants by city: {}", city);
        List<RestaurantResponse> restaurants = restaurantRepository.findByAddressContainingIgnoreCase(city).stream()
                .map(restaurant -> modelMapper.map(restaurant, RestaurantResponse.class))
                .collect(Collectors.toList());
        logger.info("Found {} restaurants in city '{}'", restaurants.size(), city);
        return restaurants;
    }

    @Override
    public List<RestaurantResponse> searchRestaurantsByName(String name) {
        logger.info("Searching restaurants by name: {}", name);
        List<RestaurantResponse> restaurants = restaurantRepository.findByNameContainingIgnoreCase(name).stream()
                .map(restaurant -> modelMapper.map(restaurant, RestaurantResponse.class))
                .collect(Collectors.toList());
        logger.info("Found {} restaurants matching name '{}'", restaurants.size(), name);
        return restaurants;
    }
}