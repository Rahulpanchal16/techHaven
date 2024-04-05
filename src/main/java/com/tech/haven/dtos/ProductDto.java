package com.tech.haven.dtos;

import java.util.Date;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class ProductDto {
    private String product_id;
    @NotBlank(message = "product title cannot be blank")
    @Min(value = 3, message = "product title should be atleast 3 characters long")
    private String title;
    private String description;
    @NotBlank(message = "product price cannot be blank")
    private double price;
    @NotBlank(message = "product quantity cannot be blank")
    private int quantity;
    private double discount;
    private Date dateAdded;
    @NotBlank(message = "please set the status of the product")
    private Boolean isLive;
    @NotBlank(message = "please set the availability of the product")
    private Boolean inStock;
    private String image;

    private CategoryDto category;
}
