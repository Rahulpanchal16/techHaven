package com.tech.haven.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.tech.haven.dtos.ProductDto;
import com.tech.haven.helpers.ApiResponse;
import com.tech.haven.helpers.ImageResponse;
import com.tech.haven.helpers.PageableResponse;
import com.tech.haven.services.FileUploadService;
import com.tech.haven.services.ProductService;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping(path = "/api/products")
public class ProductController {

	@Autowired
	private ProductService prodService;

	@Autowired
	private FileUploadService fileUploadService;

	@Value("${product.images.path}")
	private String productImgPath;

	@PostMapping(path = "/")
	public ResponseEntity<ProductDto> createProduct(@RequestBody ProductDto prodDto) {
		ProductDto createdProduct = this.prodService.createProduct(prodDto);
		return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
	}

	@PatchMapping(path = "/{prodId}")
	public ResponseEntity<ProductDto> updateProduct(@RequestBody ProductDto prodDto, @PathVariable String prodId) {
		ProductDto updatedProduct = this.prodService.updateProduct(prodDto, prodId);
		return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
	}

	@GetMapping(path = "/")
	public ResponseEntity<PageableResponse<ProductDto>> getAllProducts(
			@RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
			@RequestParam(value = "pageSize", defaultValue = "5", required = false) int pageSize,
			@RequestParam(value = "sortBy", defaultValue = "title", required = false) String sortBy,
			@RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir) {
		PageableResponse<ProductDto> allProducts = this.prodService.getAllProducts(pageNumber, pageSize, sortBy,
				sortDir);
		return new ResponseEntity<>(allProducts, HttpStatus.OK);
	}

	@GetMapping(path = "/{prodId}")
	public ResponseEntity<ProductDto> getProductById(@PathVariable String prodId) {
		ProductDto productById = this.prodService.getProductById(prodId);
		return new ResponseEntity<>(productById, HttpStatus.OK);
	}

	@GetMapping(path = "/title/")
	public ResponseEntity<PageableResponse<ProductDto>> getProductsByTitle(@RequestParam String title,
			@RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
			@RequestParam(value = "pageSize", defaultValue = "5", required = false) int pageSize,
			@RequestParam(value = "sortBy", defaultValue = "title", required = false) String sortBy,
			@RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir) {
		PageableResponse<ProductDto> productsByTitle = this.prodService.getProductsByTitle(title, pageNumber, pageSize,
				sortBy, sortDir);
		return new ResponseEntity<>(productsByTitle, HttpStatus.OK);
	}

	@GetMapping(path = "/live/")
	public ResponseEntity<PageableResponse<ProductDto>> getProductByLiveStatus(@RequestParam Boolean isLive,
			@RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
			@RequestParam(value = "pageSize", defaultValue = "5", required = false) int pageSize,
			@RequestParam(value = "sortBy", defaultValue = "title", required = false) String sortBy,
			@RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir) {
		PageableResponse<ProductDto> productsByLiveStatus = this.prodService.getProductsByLiveStatus(isLive, pageNumber,
				pageSize, sortBy, sortDir);
		return new ResponseEntity<>(productsByLiveStatus, HttpStatus.OK);
	}

	@GetMapping(path = "/stock/")
	public ResponseEntity<PageableResponse<ProductDto>> getProductByStock(@RequestParam Boolean inStock,
			@RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
			@RequestParam(value = "pageSize", defaultValue = "5", required = false) int pageSize,
			@RequestParam(value = "sortBy", defaultValue = "title", required = false) String sortBy,
			@RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir) {
		PageableResponse<ProductDto> productsByStock = this.prodService.getProductsByStock(inStock, pageNumber,
				pageSize, sortBy, sortDir);
		return new ResponseEntity<>(productsByStock, HttpStatus.OK);
	}

	@DeleteMapping(path = "/{prodId}")
	public ResponseEntity<ApiResponse> deleteProductById(@PathVariable String prodId) throws IOException {
		this.prodService.deleteProduct(prodId);
		ApiResponse response = ApiResponse.builder().message("Product deleted successfully").success(true)
				.status(HttpStatus.OK).build();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping(path = "/image/{prodId}")
	public ResponseEntity<ImageResponse> postProductImage(@RequestParam MultipartFile prodImg,
			@PathVariable String prodId) throws IOException {
		ProductDto productById = this.prodService.getProductById(prodId);
		String uploadedProductImgName = this.fileUploadService.imageUpload(prodImg, productImgPath);
		if (productById.getImage().isEmpty()) {
			productById.setImage(uploadedProductImgName);
		} else if (productById.getImage().equals("defaultProductImage.png")) {
			productById.setImage(uploadedProductImgName);
		} else {
			Files.delete(Paths.get(productImgPath + productById.getImage()));
			productById.setImage(uploadedProductImgName);
		}
		this.updateProduct(productById, prodId);
		ImageResponse response = ImageResponse.builder().imageName(uploadedProductImgName)
				.message("product image added successfully").success(true).status(HttpStatus.OK).build();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping(path = "/image/{prodId}")
	public void getProductImage(@PathVariable String prodId, HttpServletResponse response) throws IOException {
		ProductDto productById = this.prodService.getProductById(prodId);
		InputStream resource = this.fileUploadService.getResource(productImgPath, productById.getImage());
		response.setContentType(MediaType.IMAGE_JPEG_VALUE);
		StreamUtils.copy(resource, response.getOutputStream());
	}

	@GetMapping(path = "/categories/{categoryId}")
	public ResponseEntity<PageableResponse<ProductDto>> getProductsByCategory(@PathVariable String categoryId,
			@RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
			@RequestParam(value = "pageSize", defaultValue = "5", required = false) int pageSize,
			@RequestParam(value = "sortBy", defaultValue = "title", required = false) String sortBy,
			@RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir) {
		PageableResponse<ProductDto> productsByCategory = this.prodService.findByCategory(categoryId, pageNumber,
				pageSize, sortBy, sortDir);
		return new ResponseEntity<>(productsByCategory, HttpStatus.OK);
	}
}
