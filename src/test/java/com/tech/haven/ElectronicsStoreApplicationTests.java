package com.tech.haven;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.tech.haven.exceptions.ResourceNotFoundException;
import com.tech.haven.helpers.InvoiceGeneration;
import com.tech.haven.helpers.SendEmail;
import com.tech.haven.models.Cart;
import com.tech.haven.models.Role;
import com.tech.haven.models.User;
import com.tech.haven.repositories.CartRepository;
import com.tech.haven.repositories.RoleRepository;
import com.tech.haven.repositories.UserRepository;

@SpringBootTest
class ElectronicsStoreApplicationTests {

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private RoleRepository roleRepo;

	@Autowired
	private InvoiceGeneration invoice;

	@Autowired
	private SendEmail emailSend;

	@Value("${role.normal}")
	String normalRoleId;

	@Test
	void contextLoads() {
	}

	@Test
	@Disabled
	void creatingFakeUsers() {
		for (int i = 1; i < 11; i++) {
			String random_user_id = UUID.randomUUID().toString();
			Random random = new Random();
			int ran = random.nextInt(2);
			String gen = (ran == 0) ? "m" : "f";
			User user = User.builder().user_id(random_user_id).name("user " + i).email("user" + i + "@test.com")
					.password(passwordEncoder.encode("1234")).about("Test user").gender(gen).image("pic.png").build();
			this.userRepo.save(user);
		}
	}

	@Test
	@Disabled
	void createTestCart() {
		String userId = "f3842b4d-b881-4161-a502-5ef3e35899b6";
		User user = this.userRepo.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User", "user id", userId));

		Cart cart = Cart.builder().cartCreatedOn(LocalDateTime.now()).cartId(UUID.randomUUID().toString()).user(user)
				.items(new ArrayList<>()).build();
		this.cartRepository.save(cart);
	}

	@Test
	@Disabled
	void getBCryptPassword() {
		String passwordToConvert = "1234";
		String encodedPassword = this.passwordEncoder.encode(passwordToConvert);
		System.out.println("The encoded password: " + encodedPassword);
	}

	@Test
	@Disabled
	void changePasswords() {
		List<User> allUsers = this.userRepo.findAll();
		for (User user : allUsers) {
			if (user.getPassword().equals("1234")) {
				user.setPassword("$2a$10$J0DAHkG4JypjKvwhJMyUou7FDBRPLzUN960G7QggVyqDXhNC17/0e");
				System.out.println("changed password for user: " + user.getUsername());
				this.userRepo.save(user);
			}
		}

	}

	@Test
	@Disabled
	void getRoleById() {
		Role role = this.roleRepo.findById(normalRoleId)
				.orElseThrow(() -> new ResourceNotFoundException("Role", "role id", normalRoleId));
		System.out.println(role.toString());
		;
	}

	@Test
	@Disabled
	void getAllRoles() {
		List<Role> allRoles = this.roleRepo.findAll();
		allRoles.stream().forEach(System.out::println);
	}


	@Test
	void sendSimpleEmail(){
		emailSend.sendSimpleEmail("panchalrahul1603@gmail.com", "Test", "Testing smtp feature");
	}

	@Test
	@Disabled
	void invoiceGen() throws IOException {
		invoice.createPdf("3af884f8-50d9-4d36-880c-f4618c908a3b");
	}
}
