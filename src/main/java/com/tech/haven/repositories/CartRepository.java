package com.tech.haven.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tech.haven.models.Cart;
import com.tech.haven.models.User;

public interface CartRepository extends JpaRepository<Cart, String> {

	Optional<Cart> findByUser(User user);
}
