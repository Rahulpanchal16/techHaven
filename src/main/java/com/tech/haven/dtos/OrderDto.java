package com.tech.haven.dtos;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tech.haven.helpers.OrderStatus;
import com.tech.haven.helpers.PaymentStatus;

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
public class OrderDto {

	private String orderId;

	private OrderStatus orderStatus;

	private PaymentStatus paymentStatus;

	private float orderAmount;

	private String billingAddress;

	private String deliveryAddress;

	private long contactNumber;

	@Builder.Default
	@JsonFormat(pattern = "dd-MM-yyyy hh:mm a")
	private LocalDateTime orderedOn = LocalDateTime.now();

	@JsonFormat(pattern = "dd-MM-yyyy hh:mm a")
	private LocalDateTime ExpectedDeliveryDate;

	@Builder.Default
	private List<OrderItemDto> orderItems = new ArrayList<>();

}
