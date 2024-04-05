package com.tech.haven.services;

import java.io.IOException;
import java.util.List;

import com.tech.haven.dtos.OrderDto;
import com.tech.haven.helpers.CreateOrderRequest;
import com.tech.haven.helpers.PageableResponse;

import jakarta.mail.MessagingException;

public interface OrderService {

	OrderDto createOrder(CreateOrderRequest request);

	void removeOrder(String orderId);

	List<OrderDto> getOrdersByUser(String userId);

	PageableResponse<OrderDto> getAllOrders(int pageNumber, int pageSize, String sortBy, String sortDir);

	OrderDto updateOrder(String paymentStatus, String orderId)
			throws MessagingException, InterruptedException, IOException;

}
