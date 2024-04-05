package com.tech.haven.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tech.haven.models.Category;

public interface CategoryRepository extends JpaRepository<Category, String>{
	
}
