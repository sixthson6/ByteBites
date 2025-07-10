package com.tech.restaurant_service.dto;

import lombok.Data;
import java.util.List;

@Data
public class RestaurantResponse {
    private Long id;
    private String name;
    private String address;
    private List<ProductResponse> menu;
}
