package com.tech.haven.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartItemDto {
	private int cartItemId;

	private ProductDto product;

	private int quantityOrdered;

	private double totalAmount;

}
