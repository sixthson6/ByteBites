package com.tech.restaurant_service.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class RestaurantRequest {
    @NotBlank(message = "Restaurant name cannot be empty")
    @Size(min = 3, max = 100, message = "Restaurant name must be between 3 and 100 characters")
    private String name;

    @NotBlank(message = "Address cannot be empty")
    @Size(min = 5, max = 200, message = "Address must be between 5 and 200 characters")
    private String address;

    private List<ProductRequest> menu;
}
