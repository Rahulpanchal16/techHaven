package com.tech.haven.services.implementations;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.tech.haven.dtos.OrderDto;
import com.tech.haven.exceptions.BadApiRequestException;
import com.tech.haven.exceptions.ResourceNotFoundException;
import com.tech.haven.helpers.CreateOrderRequest;
import com.tech.haven.helpers.InvoiceGeneration;
import com.tech.haven.helpers.OrderStatus;
import com.tech.haven.helpers.PageToPageableHelper;
import com.tech.haven.helpers.PageableResponse;
import com.tech.haven.helpers.PaymentStatus;
import com.tech.haven.helpers.SendEmail;
import com.tech.haven.models.Cart;
import com.tech.haven.models.CartItem;
import com.tech.haven.models.Order;
import com.tech.haven.models.OrderItem;
import com.tech.haven.models.User;
import com.tech.haven.repositories.CartRepository;
import com.tech.haven.repositories.OrderRepository;
import com.tech.haven.repositories.UserRepository;
import com.tech.haven.services.OrderService;

import jakarta.mail.MessagingException;

@Service
public class OrderServiceImplementation implements OrderService {

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private OrderRepository orderRepo;

	@Autowired
	private CartRepository cartRepo;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private SendEmail sendEmail;

	@Autowired
	private InvoiceGeneration invoiceGenerate;

	@Override
	public OrderDto createOrder(CreateOrderRequest request) {

		// getting the user
		User user = this.userRepo.findById(request.getUserId())
				.orElseThrow(() -> new ResourceNotFoundException("User" + "user id" + request.getUserId()));

		Cart cart = this.cartRepo.findById(request.getCartId())
				.orElseThrow(() -> new ResourceNotFoundException("No Cart Found"));

		List<CartItem> cartItems = cart.getItems();

		if (cartItems.size() <= 0) {
			throw new BadApiRequestException("Cart is empty");
		}

		Order order = Order.builder().orderId(UUID.randomUUID().toString()).billingAddress(request.getBillingAddress())
				.deliveryAddress(request.getDeliveryAddress()).orderStatus(request.getOrderStatus())
				.paymentStatus(request.getPaymentStatus()).contactNumber(request.getContactNumber())
				.orderedOn(LocalDateTime.now()).user(user).build();

		AtomicReference<Double> totalOrderAmount = new AtomicReference<>(0.0);
		List<OrderItem> orderItems = cartItems.stream().map(item -> {

			OrderItem orderItem = OrderItem.builder().product(item.getProduct())
					.quantityOrdered(item.getQuantityOrdered())
					.totalPrice(item.getProduct().getPrice() * item.getQuantityOrdered()).order(order).build();

			totalOrderAmount.set(totalOrderAmount.get() + orderItem.getTotalPrice());
			return orderItem;

		}).collect(Collectors.toList());

		order.setOrderItems(orderItems);
		order.setOrderAmount(totalOrderAmount.get());
		order.setExpectedDeliveryDate(null);

		Order savedOrder = this.orderRepo.save(order);
		cart.getItems().clear();
		this.cartRepo.save(cart);

		return this.modelMapper.map(savedOrder, OrderDto.class);
	}

	@Override
	public void removeOrder(String orderId) {

		Order order = this.orderRepo.findById(orderId)
				.orElseThrow(() -> new ResourceNotFoundException("No Order Found"));
		this.orderRepo.delete(order);
	}

	@Override
	public List<OrderDto> getOrdersByUser(String userId) {
		// getting the user
		User user = this.userRepo.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User" + "user id" + userId));

		List<Order> orders = this.orderRepo.findByUser(user);
		for (Order order : orders) {
			System.out.println(order.toString());
		}
		List<OrderDto> orderDto = orders.stream().map(order -> this.modelMapper.map(order, OrderDto.class))
				.collect(Collectors.toList());
		return orderDto;
	}

	@Override
	public PageableResponse<OrderDto> getAllOrders(int pageNumber, int pageSize, String sortBy, String sortDir) {
		Sort sort = (sortDir.trim().substring(0, 1).equals("d")) ? Sort.by(sortBy).descending()
				: Sort.by(sortBy).ascending();
		Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
		Page<Order> page = this.orderRepo.findAll(pageable);
		PageableResponse<OrderDto> response = PageToPageableHelper.getPageableResponse(page, OrderDto.class);
		return response;
	}

	@Override
	public OrderDto updateOrder(String paymentStatus, String orderId)
			throws MessagingException, InterruptedException, IOException {

		Order order = this.orderRepo.findById(orderId)
				.orElseThrow(() -> new ResourceNotFoundException("No order found"));

		if (paymentStatus.trim().equalsIgnoreCase("completed")) {
			order.setOrderStatus(OrderStatus.PROCESSING);
			order.setPaymentStatus(PaymentStatus.COMPLETED);
			order.setExpectedDeliveryDate(order.getOrderedOn().plusDays(5));
			invoiceGenerate.createPdf(orderId);
			sendEmail.sendEmail(order.getUser().getEmail(), "Order Invoice", "Order Invoice",
					order.getUser().getName(), orderId);
		}
		Order updatedOrder = this.orderRepo.save(order);
		return this.modelMapper.map(updatedOrder, OrderDto.class);
	}

}
