package com.tech.haven.services;

import java.io.IOException;

import com.tech.haven.dtos.ProductDto;
import com.tech.haven.helpers.PageableResponse;

public interface ProductService {
	// create product
	ProductDto createProduct(ProductDto productDto);

	// get Product by Id
	ProductDto getProductById(String prodId);

	// get all Products
	PageableResponse<ProductDto> getAllProducts(int pageNumber, int pageSize, String sortBy, String sortDir);

	// get Products by title
	PageableResponse<ProductDto> getProductsByTitle(String prodTitle, int pageNumber, int pageSize, String sortBy,
			String sortDir);

	// get Products by live status
	PageableResponse<ProductDto> getProductsByLiveStatus(Boolean isLive, int pageNumber, int pageSize, String sortBy,
			String sortDir);

	// get products by stock status
	PageableResponse<ProductDto> getProductsByStock(Boolean inStock, int pageNumber, int pageSize, String sortBy,
			String sortDir);

	// update Product information
	ProductDto updateProduct(ProductDto prodDto, String prodId);

	// delete Product using product id
	void deleteProduct(String prodId) throws IOException;

	// creating a product with category
	ProductDto createWithCategory(ProductDto prodDto, String categoryId);

	// assigning category to existing products
	ProductDto assignCategory(String categoryId, String prodId);

	PageableResponse<ProductDto> findByCategory(String categoryId, int pageNumber, int pageSize, String sortBy,
			String sortDir);

}
