package com.tech.haven.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tech.haven.dtos.CartDto;
import com.tech.haven.helpers.AddItemToCartRequest;
import com.tech.haven.helpers.ApiResponse;
import com.tech.haven.helpers.CartDtoResponse;
import com.tech.haven.services.CartService;

@RestController
@RequestMapping(path = "/api/carts")
public class CartController {

	@Autowired
	private CartService cartService;

	@PostMapping(path = "/user/{userId}")
	public ResponseEntity<CartDto> addItemsToCart(@PathVariable String userId,
			@RequestBody AddItemToCartRequest request) {
		CartDto cartDto = this.cartService.addItemToCart(userId, request);
		return new ResponseEntity<>(cartDto, HttpStatus.CREATED);
	}

	@GetMapping(path = "/user/{userId}")
	public ResponseEntity<CartDto> getCartByUserId(@PathVariable String userId) {
		CartDto cartDto = this.cartService.getCartByUserId(userId);
		return new ResponseEntity<>(cartDto, HttpStatus.OK);
	}

	@DeleteMapping(path = "/user/{userId}/itemId/{cartItemId}")
	public ResponseEntity<CartDtoResponse> removeItemFromCart(@PathVariable String userId,
			@PathVariable int cartItemId) {
		CartDtoResponse response = this.cartService.removeItemFromCart(userId, cartItemId);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@DeleteMapping(path = "/user/{userId}")
	public ResponseEntity<ApiResponse> clearCart(@PathVariable String userId) {
		this.cartService.clearCart(userId);
		ApiResponse response = ApiResponse.builder().message("Cart cleared successfully").success(true)
				.status(HttpStatus.OK).build();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
