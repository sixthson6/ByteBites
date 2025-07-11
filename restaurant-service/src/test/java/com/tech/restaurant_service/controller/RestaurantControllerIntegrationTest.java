package com.tech.restaurant_service.controller;

import com.tech.restaurant_service.config.TestConfig;
import com.tech.restaurant_service.dto.ProductRequest;
import com.tech.restaurant_service.dto.ProductResponse;
import com.tech.restaurant_service.dto.RestaurantRequest;
import com.tech.restaurant_service.dto.RestaurantResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(TestConfig.class)
public class RestaurantControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl;
    private HttpHeaders headers;

    private final String ADMIN_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBleGFtcGxlLmNvbSIsImlhdCI6MTY3ODkwNTYwMCwiZXhwIjoxNjc4OTEyODAwfQ.someAdminToken";
    private final String USER_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyQGV4YW1wbGUuY29tIiwiaWF0IjoxNjc4OTA1NjAwLCJleHAiOjE2Nzg5MTI4MDB9.someUserToken";

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/v1/restaurants";
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
    }

    private HttpEntity<RestaurantRequest> createHttpEntity(RestaurantRequest request, String token) {
        HttpHeaders authHeaders = new HttpHeaders();
        authHeaders.setContentType(MediaType.APPLICATION_JSON);
        authHeaders.setBearerAuth(token);
        return new HttpEntity<>(request, authHeaders);
    }

    private HttpEntity<ProductRequest> createProductHttpEntity(ProductRequest request, String token) {
        HttpHeaders authHeaders = new HttpHeaders();
        authHeaders.setContentType(MediaType.APPLICATION_JSON);
        authHeaders.setBearerAuth(token);
        return new HttpEntity<>(request, authHeaders);
    }

    private HttpEntity<Void> createHttpEntityWithToken(String token) {
        HttpHeaders authHeaders = new HttpHeaders();
        authHeaders.setBearerAuth(token);
        return new HttpEntity<>(authHeaders);
    }

    @Test
    void createRestaurant_shouldReturnCreatedRestaurant_withAdminToken() {
        RestaurantRequest request = new RestaurantRequest();
        request.setName("Test Restaurant");
        request.setAddress("123 Test St");

        ResponseEntity<RestaurantResponse> response = restTemplate.postForEntity(baseUrl, createHttpEntity(request, ADMIN_TOKEN), RestaurantResponse.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Test Restaurant", response.getBody().getName());
    }

    @Test
    void createRestaurant_shouldReturnForbidden_withUserToken() {
        RestaurantRequest request = new RestaurantRequest();
        request.setName("Test Restaurant");
        request.setAddress("123 Test St");

        ResponseEntity<RestaurantResponse> response = restTemplate.postForEntity(baseUrl, createHttpEntity(request, USER_TOKEN), RestaurantResponse.class);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void createRestaurant_shouldReturnUnauthorized_noToken() {
        RestaurantRequest request = new RestaurantRequest();
        request.setName("Test Restaurant");
        request.setAddress("123 Test St");

        ResponseEntity<RestaurantResponse> response = restTemplate.postForEntity(baseUrl, new HttpEntity<>(request, headers), RestaurantResponse.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void getAllRestaurants_shouldReturnListOfRestaurants() {
        restTemplate.postForEntity(baseUrl, createHttpEntity(new RestaurantRequest("Restaurant A", "Address A", null), ADMIN_TOKEN), RestaurantResponse.class);

        ResponseEntity<List> response = restTemplate.exchange(baseUrl, HttpMethod.GET, createHttpEntityWithToken(USER_TOKEN), List.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
    }

    @Test
    void getRestaurantById_shouldReturnRestaurant() {
        RestaurantRequest createRequest = new RestaurantRequest();
        createRequest.setName("Another Restaurant");
        createRequest.setAddress("456 Another St");
        ResponseEntity<RestaurantResponse> createResponse = restTemplate.postForEntity(baseUrl, createHttpEntity(createRequest, ADMIN_TOKEN), RestaurantResponse.class);
        Long restaurantId = createResponse.getBody().getId();

        ResponseEntity<RestaurantResponse> response = restTemplate.exchange(baseUrl + "/" + restaurantId, HttpMethod.GET, createHttpEntityWithToken(USER_TOKEN), RestaurantResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Another Restaurant", response.getBody().getName());
    }

    @Test
    void updateRestaurant_shouldReturnUpdatedRestaurant_withAdminToken() {
        RestaurantRequest createRequest = new RestaurantRequest();
        createRequest.setName("Old Name");
        createRequest.setAddress("Old Address");
        ResponseEntity<RestaurantResponse> createResponse = restTemplate.postForEntity(baseUrl, createHttpEntity(createRequest, ADMIN_TOKEN), RestaurantResponse.class);
        Long restaurantId = createResponse.getBody().getId();

        RestaurantRequest updateRequest = new RestaurantRequest();
        updateRequest.setName("New Name");
        updateRequest.setAddress("New Address");

        ResponseEntity<RestaurantResponse> response = restTemplate.exchange(baseUrl + "/" + restaurantId, HttpMethod.PUT, createHttpEntity(updateRequest, ADMIN_TOKEN), RestaurantResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("New Name", response.getBody().getName());
    }

    @Test
    void deleteRestaurant_shouldReturnNoContent_withAdminToken() {
        RestaurantRequest createRequest = new RestaurantRequest();
        createRequest.setName("To Be Deleted");
        createRequest.setAddress("Delete Address");
        ResponseEntity<RestaurantResponse> createResponse = restTemplate.postForEntity(baseUrl, createHttpEntity(createRequest, ADMIN_TOKEN), RestaurantResponse.class);
        Long restaurantId = createResponse.getBody().getId();

        ResponseEntity<Void> response = restTemplate.exchange(baseUrl + "/" + restaurantId, HttpMethod.DELETE, createHttpEntityWithToken(ADMIN_TOKEN), Void.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void addMenuItem_shouldReturnCreatedMenuItem_withAdminToken() {
        RestaurantRequest createRestaurantRequest = new RestaurantRequest();
        createRestaurantRequest.setName("Restaurant for Menu");
        createRestaurantRequest.setAddress("Menu Address");
        ResponseEntity<RestaurantResponse> createRestaurantResponse = restTemplate.postForEntity(baseUrl, createHttpEntity(createRestaurantRequest, ADMIN_TOKEN), RestaurantResponse.class);
        Long restaurantId = createRestaurantResponse.getBody().getId();

        ProductRequest productRequest = new ProductRequest();
        productRequest.setName("Pizza");
        productRequest.setDescription("Delicious pizza");
        productRequest.setPrice(15.00);

        ResponseEntity<ProductResponse> response = restTemplate.postForEntity(baseUrl + "/" + restaurantId + "/menu", createProductHttpEntity(productRequest, ADMIN_TOKEN), ProductResponse.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Pizza", response.getBody().getName());
    }

    @Test
    void getMenuForRestaurant_shouldReturnListOfMenuItems() {
        RestaurantRequest createRestaurantRequest = new RestaurantRequest();
        createRestaurantRequest.setName("Restaurant with Menu");
        createRestaurantRequest.setAddress("Menu Address 2");
        ResponseEntity<RestaurantResponse> createRestaurantResponse = restTemplate.postForEntity(baseUrl, createHttpEntity(createRestaurantRequest, ADMIN_TOKEN), RestaurantResponse.class);
        Long restaurantId = createRestaurantResponse.getBody().getId();

        ProductRequest productRequest = new ProductRequest();
        productRequest.setName("Burger");
        productRequest.setDescription("Tasty burger");
        productRequest.setPrice(10.00);
        restTemplate.postForEntity(baseUrl + "/" + restaurantId + "/menu", createProductHttpEntity(productRequest, ADMIN_TOKEN), ProductResponse.class);

        ResponseEntity<List> response = restTemplate.exchange(baseUrl + "/" + restaurantId + "/menu", HttpMethod.GET, createHttpEntityWithToken(USER_TOKEN), List.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
    }

    @Test
    void searchRestaurantsByName_shouldReturnMatchingRestaurants() {
        RestaurantRequest createRequest = new RestaurantRequest();
        createRequest.setName("Search Test Restaurant");
        createRequest.setAddress("Search Address");
        restTemplate.postForEntity(baseUrl, createHttpEntity(createRequest, ADMIN_TOKEN), RestaurantResponse.class);

        ResponseEntity<List> response = restTemplate.exchange(baseUrl + "/search?name=Search Test Restaurant", HttpMethod.GET, createHttpEntityWithToken(USER_TOKEN), List.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
    }
}