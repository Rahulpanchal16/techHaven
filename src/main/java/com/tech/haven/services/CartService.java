package com.tech.haven.services;

import com.tech.haven.dtos.CartDto;
import com.tech.haven.helpers.AddItemToCartRequest;
import com.tech.haven.helpers.CartDtoResponse;

public interface CartService {

	// Add products to cart
	CartDto addItemToCart(String userId, AddItemToCartRequest request);

	// Remove products from cart
	CartDtoResponse removeItemFromCart(String userId, int cartItemId);

	// Clear cart
	void clearCart(String userId);

	// get cart with cart items using user id
	CartDto getCartByUserId(String userId);
}
