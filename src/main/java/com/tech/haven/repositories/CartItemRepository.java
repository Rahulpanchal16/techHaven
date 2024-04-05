package com.tech.haven.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tech.haven.models.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> {

}
