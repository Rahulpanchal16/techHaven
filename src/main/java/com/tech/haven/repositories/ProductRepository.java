package com.tech.haven.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tech.haven.models.Category;
import com.tech.haven.models.Product;

public interface ProductRepository extends JpaRepository<Product, String> {

	Page<Product> findByTitleContaining(String title, Pageable pageable);

	Page<Product> findByInStock(Boolean inStock, Pageable pageable);

	List<Product> findByDiscount(double discount);

	@Query("SELECT p FROM Product p WHERE p.dateAdded BETWEEN :startDate AND :endDate")
	List<Product> findProductsByDateRange(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

	List<Product> findByDateAdded(Date dateAdded);

	Page<Product> findByIsLive(Boolean islive, Pageable pageable);

	Page<Product> findByCategory(Category category, Pageable pageable);
}
