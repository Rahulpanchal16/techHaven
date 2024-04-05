package com.tech.haven.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.tech.haven.helpers.OrderStatus;
import com.tech.haven.helpers.PaymentStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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
@Entity
@Table(name = "orders")
public class Order {

	@Id
	private String orderId;

	@Enumerated(EnumType.STRING)
	private OrderStatus orderStatus;

	@Enumerated(EnumType.STRING)
	private PaymentStatus paymentStatus;

	private double orderAmount;

	@Column(length = 1000)
	private String billingAddress;

	@Column(length = 1000)
	private String deliveryAddress;

	private long contactNumber;

	private LocalDateTime orderedOn;

	private LocalDateTime ExpectedDeliveryDate;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id")
	private User user;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "order")
	@Builder.Default
	private List<OrderItem> orderItems = new ArrayList<>();

}
