package com.tech.haven.services;

import java.io.IOException;

import com.tech.haven.dtos.CategoryDto;
import com.tech.haven.helpers.PageableResponse;

public interface CategoryService {

	CategoryDto createCategory(CategoryDto catDto);

	CategoryDto getCategoryById(String catId);

	PageableResponse<CategoryDto> getAllCategories(int pageNo, int pageSize, String sortDir, String sortBy);

	void deleteCategory(String catId) throws IOException;

	CategoryDto updateCategory(CategoryDto categoryDto, String catId);

}
