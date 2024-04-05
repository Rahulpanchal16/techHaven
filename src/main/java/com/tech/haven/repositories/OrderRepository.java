package com.tech.haven.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tech.haven.models.Order;
import com.tech.haven.models.User;

public interface OrderRepository extends JpaRepository<Order, String> {

	List<Order> findByUser(User user);
}
