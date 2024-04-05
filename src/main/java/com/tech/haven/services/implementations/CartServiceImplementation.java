package com.tech.haven.services.implementations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.tech.haven.dtos.CartDto;
import com.tech.haven.exceptions.ResourceNotFoundException;
import com.tech.haven.helpers.AddItemToCartRequest;
import com.tech.haven.helpers.CartDtoResponse;
import com.tech.haven.models.Cart;
import com.tech.haven.models.CartItem;
import com.tech.haven.models.Product;
import com.tech.haven.models.User;
import com.tech.haven.repositories.CartItemRepository;
import com.tech.haven.repositories.CartRepository;
import com.tech.haven.repositories.ProductRepository;
import com.tech.haven.repositories.UserRepository;
import com.tech.haven.services.CartService;

@Service
public class CartServiceImplementation implements CartService {

	@Autowired
	private CartRepository cartRepo;

	@Autowired
	private ProductRepository prodRepo;

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private CartItemRepository cartItemRepo;

	@Autowired
	private ModelMapper modelMapper;

	@Override
	public CartDto addItemToCart(String userId, AddItemToCartRequest request) {

		int quantityOrdered = request.getQuantityOrdered();
		String productId = request.getProductId();

		// fetch the product
		Product product = this.prodRepo.findById(productId)
				.orElseThrow(() -> new ResourceNotFoundException("Product", "product id", productId));

		User user = this.userRepo.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User", "user id", userId));

		Cart cart = null;
		try {
			cart = this.cartRepo.findByUser(user).get();
		} catch (NoSuchElementException ex) {
			cart = new Cart();
			cart.setCartId(UUID.randomUUID().toString());
			cart.setCartCreatedOn(LocalDateTime.now());
			cart.setUser(user);
		}

		AtomicReference<Boolean> updated = new AtomicReference<>(false);
		List<CartItem> items = cart.getItems();
		items = items.stream().map(item -> {
			if (item.getProduct().getProduct_id().equals(product.getProduct_id())) {
				// item already present in the cart
				int presentQuantityOrdered = item.getQuantityOrdered();
				item.setQuantityOrdered(quantityOrdered + presentQuantityOrdered);
				item.setTotalAmount(quantityOrdered * product.getPrice());
				updated.set(true);
			}
			return item;
		}).collect(Collectors.toList());

		if (!updated.get()) {
			CartItem cartItem = CartItem.builder().cart(cart).product(product).quantityOrdered(quantityOrdered)
					.totalAmount(quantityOrdered * product.getPrice()).build();

			cart.getItems().add(cartItem);
		}

		Cart updatedCart = this.cartRepo.save(cart);

		return this.modelMapper.map(updatedCart, CartDto.class);

	}

	@Override
	public CartDtoResponse removeItemFromCart(String userId, int cartItemId) {
		this.cartItemRepo.deleteById(cartItemId);
		User user = this.userRepo.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User", "user id", userId));

		Cart cart = this.cartRepo.findByUser(user).get();
		CartDto cartDto = this.modelMapper.map(cart, CartDto.class);
		return CartDtoResponse.builder().cartDto(cartDto).message("Product deleted successfully").success(true)
				.status(HttpStatus.OK).build();
	}

	@Override
	public void clearCart(String userId) {
		User user = this.userRepo.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User", "user id", userId));

		Cart cart = this.cartRepo.findByUser(user)
				.orElseThrow(() -> new ResourceNotFoundException("Cart for this user does not exist"));
		List<CartItem> items = cart.getItems();
		items.clear();
		this.cartRepo.save(cart);

	}

	@Override
	public CartDto getCartByUserId(String userId) {
		User user = this.userRepo.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User", "user id", userId));
		Cart cart = null;
		try {
			cart = this.cartRepo.findByUser(user).get();
		} catch (NoSuchElementException e) {
			cart = new Cart();
			cart.setCartId(UUID.randomUUID().toString());
			cart.setCartCreatedOn(LocalDateTime.now());
			cart.setUser(user);
		}
		Cart updatedCart = this.cartRepo.save(cart);
		return this.modelMapper.map(updatedCart, CartDto.class);
	}

}
