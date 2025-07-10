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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestaurantServiceImplTest {

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private RestaurantServiceImpl restaurantService;

    private Restaurant restaurant;
    private RestaurantRequest restaurantRequest;
    private RestaurantResponse restaurantResponse;
    private Product product;
    private ProductRequest productRequest;
    private ProductResponse productResponse;

    @BeforeEach
    void setUp() {
        restaurant = Restaurant.builder()
                .id(1L)
                .name("Test Restaurant")
                .address("123 Test St")
                .menu(Arrays.asList())
                .build();

        restaurantRequest = new RestaurantRequest();
        restaurantRequest.setName("Test Restaurant");
        restaurantRequest.setAddress("123 Test St");

        restaurantResponse = new RestaurantResponse();
        restaurantResponse.setId(1L);
        restaurantResponse.setName("Test Restaurant");
        restaurantResponse.setAddress("123 Test St");

        product = Product.builder()
                .id(101L)
                .name("Test Product")
                .description("Description")
                .price(BigDecimal.valueOf(10.00))
                .restaurant(restaurant)
                .build();

        productRequest = new ProductRequest();
        productRequest.setName("Test Product");
        productRequest.setDescription("Description");
        productRequest.setPrice(10.00);

        productResponse = new ProductResponse();
        productResponse.setId(101L);
        productResponse.setName("Test Product");
        productResponse.setDescription("Description");
        productResponse.setPrice(10.00);
    }

    @Test
    void getAllRestaurants_shouldReturnListOfRestaurants() {
        when(restaurantRepository.findAll()).thenReturn(Arrays.asList(restaurant));
        when(modelMapper.map(any(Restaurant.class), eq(RestaurantResponse.class))).thenReturn(restaurantResponse);

        List<RestaurantResponse> result = restaurantService.getAllRestaurants();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(restaurantResponse, result.get(0));
        verify(restaurantRepository, times(1)).findAll();
        verify(modelMapper, times(1)).map(any(Restaurant.class), eq(RestaurantResponse.class));
    }

    @Test
    void getRestaurantById_shouldReturnRestaurant_whenFound() {
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));
        when(modelMapper.map(any(Restaurant.class), eq(RestaurantResponse.class))).thenReturn(restaurantResponse);

        Optional<RestaurantResponse> result = restaurantService.getRestaurantById(1L);

        assertTrue(result.isPresent());
        assertEquals(restaurantResponse, result.get());
        verify(restaurantRepository, times(1)).findById(1L);
        verify(modelMapper, times(1)).map(any(Restaurant.class), eq(RestaurantResponse.class));
    }

    @Test
    void getRestaurantById_shouldReturnEmptyOptional_whenNotFound() {
        when(restaurantRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<RestaurantResponse> result = restaurantService.getRestaurantById(1L);

        assertFalse(result.isPresent());
        verify(restaurantRepository, times(1)).findById(1L);
        verify(modelMapper, never()).map(any(), any());
    }

    @Test
    void createRestaurant_shouldReturnCreatedRestaurant() {
        when(modelMapper.map(any(RestaurantRequest.class), eq(Restaurant.class))).thenReturn(restaurant);
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(restaurant);
        when(modelMapper.map(any(Restaurant.class), eq(RestaurantResponse.class))).thenReturn(restaurantResponse);

        RestaurantResponse result = restaurantService.createRestaurant(restaurantRequest);

        assertNotNull(result);
        assertEquals(restaurantResponse, result);
        verify(modelMapper, times(1)).map(restaurantRequest, Restaurant.class);
        verify(restaurantRepository, times(1)).save(restaurant);
        verify(modelMapper, times(1)).map(restaurant, RestaurantResponse.class);
    }

    @Test
    void updateRestaurant_shouldReturnUpdatedRestaurant_whenFound() {
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(restaurant);
        when(modelMapper.map(any(Restaurant.class), eq(RestaurantResponse.class))).thenReturn(restaurantResponse);

        RestaurantResponse result = restaurantService.updateRestaurant(1L, restaurantRequest);

        assertNotNull(result);
        assertEquals(restaurantResponse, result);
        verify(restaurantRepository, times(1)).findById(1L);
        verify(restaurantRepository, times(1)).save(restaurant);
        verify(modelMapper, times(1)).map(restaurant, RestaurantResponse.class);
    }

    @Test
    void updateRestaurant_shouldThrowResourceNotFoundException_whenNotFound() {
        when(restaurantRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> restaurantService.updateRestaurant(1L, restaurantRequest));
        verify(restaurantRepository, times(1)).findById(1L);
        verify(restaurantRepository, never()).save(any());
        verify(modelMapper, never()).map(any(), any());
    }

    @Test
    void deleteRestaurant_shouldDeleteRestaurant() {
        doNothing().when(restaurantRepository).deleteById(1L);

        restaurantService.deleteRestaurant(1L);

        verify(restaurantRepository, times(1)).deleteById(1L);
    }

    @Test
    void addMenuItem_shouldReturnAddedMenuItem_whenRestaurantFound() {
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));
        when(modelMapper.map(any(ProductRequest.class), eq(Product.class))).thenReturn(product);
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(modelMapper.map(any(Product.class), eq(ProductResponse.class))).thenReturn(productResponse);

        ProductResponse result = restaurantService.addMenuItem(1L, productRequest);

        assertNotNull(result);
        assertEquals(productResponse, result);
        verify(restaurantRepository, times(1)).findById(1L);
        verify(modelMapper, times(1)).map(productRequest, Product.class);
        verify(productRepository, times(1)).save(product);
        verify(modelMapper, times(1)).map(product, ProductResponse.class);
    }

    @Test
    void addMenuItem_shouldThrowResourceNotFoundException_whenRestaurantNotFound() {
        when(restaurantRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> restaurantService.addMenuItem(1L, productRequest));
        verify(restaurantRepository, times(1)).findById(1L);
        verify(modelMapper, never()).map(any(ProductRequest.class), eq(Product.class));
        verify(productRepository, never()).save(any());
    }

    @Test
    void updateMenuItem_shouldReturnUpdatedMenuItem_whenFoundAndBelongsToRestaurant() {
        when(productRepository.findById(101L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(modelMapper.map(any(Product.class), eq(ProductResponse.class))).thenReturn(productResponse);

        ProductResponse result = restaurantService.updateMenuItem(1L, 101L, productRequest);

        assertNotNull(result);
        assertEquals(productResponse, result);
        verify(productRepository, times(1)).findById(101L);
        verify(productRepository, times(1)).save(product);
        verify(modelMapper, times(1)).map(product, ProductResponse.class);
    }

    @Test
    void updateMenuItem_shouldThrowResourceNotFoundException_whenMenuItemNotFound() {
        when(productRepository.findById(101L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> restaurantService.updateMenuItem(1L, 101L, productRequest));
        verify(productRepository, times(1)).findById(101L);
        verify(productRepository, never()).save(any());
    }

    @Test
    void updateMenuItem_shouldThrowAccessDeniedException_whenMenuItemDoesNotBelongToRestaurant() {
        Product anotherProduct = Product.builder().id(102L).name("Another Product").restaurant(Restaurant.builder().id(2L).build()).build();
        when(productRepository.findById(102L)).thenReturn(Optional.of(anotherProduct));

        assertThrows(AccessDeniedException.class, () -> restaurantService.updateMenuItem(1L, 102L, productRequest));
        verify(productRepository, times(1)).findById(102L);
        verify(productRepository, never()).save(any());
    }

    @Test
    void deleteMenuItem_shouldDeleteMenuItem_whenFoundAndBelongsToRestaurant() {
        when(productRepository.findById(101L)).thenReturn(Optional.of(product));
        doNothing().when(productRepository).delete(any(Product.class));

        restaurantService.deleteMenuItem(1L, 101L);

        verify(productRepository, times(1)).findById(101L);
        verify(productRepository, times(1)).delete(product);
    }

    @Test
    void deleteMenuItem_shouldThrowResourceNotFoundException_whenMenuItemNotFound() {
        when(productRepository.findById(101L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> restaurantService.deleteMenuItem(1L, 101L));
        verify(productRepository, times(1)).findById(101L);
        verify(productRepository, never()).delete(any());
    }

    @Test
    void deleteMenuItem_shouldThrowAccessDeniedException_whenMenuItemDoesNotBelongToRestaurant() {
        Product anotherProduct = Product.builder().id(102L).name("Another Product").restaurant(Restaurant.builder().id(2L).build()).build();
        when(productRepository.findById(102L)).thenReturn(Optional.of(anotherProduct));

        assertThrows(AccessDeniedException.class, () -> restaurantService.deleteMenuItem(1L, 102L));
        verify(productRepository, times(1)).findById(102L);
        verify(productRepository, never()).delete(any());
    }

    @Test
    void getMenuForRestaurant_shouldReturnListOfProducts_whenRestaurantFound() {
        restaurant.setMenu(Arrays.asList(product));
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));
        when(modelMapper.map(any(Product.class), eq(ProductResponse.class))).thenReturn(productResponse);

        List<ProductResponse> result = restaurantService.getMenuForRestaurant(1L);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(productResponse, result.get(0));
        verify(restaurantRepository, times(1)).findById(1L);
        verify(modelMapper, times(1)).map(any(Product.class), eq(ProductResponse.class));
    }

    @Test
    void getMenuForRestaurant_shouldThrowResourceNotFoundException_whenRestaurantNotFound() {
        when(restaurantRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> restaurantService.getMenuForRestaurant(1L));
        verify(restaurantRepository, times(1)).findById(1L);
        verify(modelMapper, never()).map(any(), any());
    }

    @Test
    void getMenuItemById_shouldReturnProduct_whenFoundAndBelongsToRestaurant() {
        when(productRepository.findById(101L)).thenReturn(Optional.of(product));
        when(modelMapper.map(any(Product.class), eq(ProductResponse.class))).thenReturn(productResponse);

        Optional<ProductResponse> result = restaurantService.getMenuItemById(1L, 101L);

        assertTrue(result.isPresent());
        assertEquals(productResponse, result.get());
        verify(productRepository, times(1)).findById(101L);
        verify(modelMapper, times(1)).map(any(Product.class), eq(ProductResponse.class));
    }

    @Test
    void getMenuItemById_shouldReturnEmptyOptional_whenMenuItemNotFound() {
        when(productRepository.findById(101L)).thenReturn(Optional.empty());

        Optional<ProductResponse> result = restaurantService.getMenuItemById(1L, 101L);

        assertFalse(result.isPresent());
        verify(productRepository, times(1)).findById(101L);
        verify(modelMapper, never()).map(any(), any());
    }

    @Test
    void getMenuItemById_shouldReturnEmptyOptional_whenMenuItemDoesNotBelongToRestaurant() {
        Product anotherProduct = Product.builder().id(102L).name("Another Product").restaurant(Restaurant.builder().id(2L).build()).build();
        when(productRepository.findById(102L)).thenReturn(Optional.of(anotherProduct));

        Optional<ProductResponse> result = restaurantService.getMenuItemById(1L, 102L);

        assertFalse(result.isPresent());
        verify(productRepository, times(1)).findById(102L);
        verify(modelMapper, never()).map(any(), any());
    }

    @Test
    void getAllMenuItems_shouldReturnListOfProducts() {
        when(productRepository.findAll()).thenReturn(Arrays.asList(product));
        when(modelMapper.map(any(Product.class), eq(ProductResponse.class))).thenReturn(productResponse);

        List<ProductResponse> result = restaurantService.getAllMenuItems();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(productResponse, result.get(0));
        verify(productRepository, times(1)).findAll();
        verify(modelMapper, times(1)).map(any(Product.class), eq(ProductResponse.class));
    }

    @Test
    void searchMenuItemsByName_shouldReturnMatchingProducts() {
        when(productRepository.findByNameContainingIgnoreCase("Test")).thenReturn(Arrays.asList(product));
        when(modelMapper.map(any(Product.class), eq(ProductResponse.class))).thenReturn(productResponse);

        List<ProductResponse> result = restaurantService.searchMenuItemsByName("Test");

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(productResponse, result.get(0));
        verify(productRepository, times(1)).findByNameContainingIgnoreCase("Test");
        verify(modelMapper, times(1)).map(any(Product.class), eq(ProductResponse.class));
    }

    @Test
    void searchRestaurantsByMenuItem_shouldReturnMatchingRestaurants() {
        when(restaurantRepository.findByMenuNameContainingIgnoreCase("Test")).thenReturn(Arrays.asList(restaurant));
        when(modelMapper.map(any(Restaurant.class), eq(RestaurantResponse.class))).thenReturn(restaurantResponse);

        List<RestaurantResponse> result = restaurantService.searchRestaurantsByMenuItem("Test");

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(restaurantResponse, result.get(0));
        verify(restaurantRepository, times(1)).findByMenuNameContainingIgnoreCase("Test");
        verify(modelMapper, times(1)).map(any(Restaurant.class), eq(RestaurantResponse.class));
    }

    @Test
    void searchRestaurantsByCity_shouldReturnMatchingRestaurants() {
        when(restaurantRepository.findByAddressContainingIgnoreCase("Test")).thenReturn(Arrays.asList(restaurant));
        when(modelMapper.map(any(Restaurant.class), eq(RestaurantResponse.class))).thenReturn(restaurantResponse);

        List<RestaurantResponse> result = restaurantService.searchRestaurantsByCity("Test");

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(restaurantResponse, result.get(0));
        verify(restaurantRepository, times(1)).findByAddressContainingIgnoreCase("Test");
        verify(modelMapper, times(1)).map(any(Restaurant.class), eq(RestaurantResponse.class));
    }

    @Test
    void searchRestaurantsByName_shouldReturnMatchingRestaurants() {
        when(restaurantRepository.findByNameContainingIgnoreCase("Test")).thenReturn(Arrays.asList(restaurant));
        when(modelMapper.map(any(Restaurant.class), eq(RestaurantResponse.class))).thenReturn(restaurantResponse);

        List<RestaurantResponse> result = restaurantService.searchRestaurantsByName("Test");

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(restaurantResponse, result.get(0));
        verify(restaurantRepository, times(1)).findByNameContainingIgnoreCase("Test");
        verify(modelMapper, times(1)).map(any(Restaurant.class), eq(RestaurantResponse.class));
    }
}
