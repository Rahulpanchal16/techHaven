package com.tech.haven.helpers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.tech.haven.dtos.OrderItemDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class CreateOrderRequest {

	private String userId;
	private String cartId;
	@Builder.Default
	private OrderStatus orderStatus = OrderStatus.ORDERED;

	@Builder.Default
	private PaymentStatus paymentStatus = PaymentStatus.PENDING;

	private String billingAddress;

	private String deliveryAddress;

	private long contactNumber;

	@Builder.Default
	private LocalDateTime orderedOn = LocalDateTime.now();

	private LocalDateTime deliveredOn;

	@Builder.Default
	private List<OrderItemDto> orderItems = new ArrayList<>();

}
