package com.tech.haven.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tech.haven.models.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {

}
