package com.tech.haven.controllers;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tech.haven.dtos.OrderDto;
import com.tech.haven.helpers.ApiResponse;
import com.tech.haven.helpers.CreateOrderRequest;
import com.tech.haven.helpers.PageableResponse;
import com.tech.haven.services.OrderService;

import jakarta.mail.MessagingException;

@RestController
@RequestMapping(path = "/api/orders")
public class OrderController {

	@Autowired
	private OrderService orderService;

	@PostMapping(path = "/")
	public ResponseEntity<OrderDto> createOrder(@RequestBody CreateOrderRequest request) {
		OrderDto orderDto = this.orderService.createOrder(request);
		return new ResponseEntity<>(orderDto, HttpStatus.CREATED);
	}

	@DeleteMapping(path = "/{orderId}")
	public ResponseEntity<ApiResponse> removeOrder(@PathVariable String orderId) {
		this.orderService.removeOrder(orderId);
		ApiResponse response = ApiResponse.builder().message("Order removed successfully").success(true)
				.status(HttpStatus.OK).build();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping(path = "/user/{userId}")
	public ResponseEntity<List<OrderDto>> getOrdersByUser(@PathVariable String userId) {
		List<OrderDto> ordersByUser = this.orderService.getOrdersByUser(userId);
		return new ResponseEntity<>(ordersByUser, HttpStatus.OK);
	}

	@GetMapping(path = "/")
	public ResponseEntity<PageableResponse<OrderDto>> getAllOrders(
			@RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
			@RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
			@RequestParam(value = "sortBy", defaultValue = "orderedOn", required = false) String sortBy,
			@RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir) {

		PageableResponse<OrderDto> allOrders = this.orderService.getAllOrders(pageNo, pageSize, sortBy, sortDir);
		return new ResponseEntity<>(allOrders, HttpStatus.OK);
	}

	@PatchMapping(path = "/{orderId}")
	public ResponseEntity<OrderDto> updateOrder(@RequestParam String paymentStatus, @PathVariable String orderId)
			throws MessagingException, InterruptedException, IOException {
		OrderDto updatedOrderDto = this.orderService.updateOrder(paymentStatus, orderId);
		return new ResponseEntity<>(updatedOrderDto, HttpStatus.OK);
	}

}
