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

import com.tech.haven.dtos.CategoryDto;
import com.tech.haven.dtos.ProductDto;
import com.tech.haven.helpers.ApiResponse;
import com.tech.haven.helpers.ImageResponse;
import com.tech.haven.helpers.PageableResponse;
import com.tech.haven.services.CategoryService;
import com.tech.haven.services.FileUploadService;
import com.tech.haven.services.ProductService;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping(path = "/api/categories")
public class CategoryController {

	@Autowired
	private CategoryService catService;

	@Autowired
	private FileUploadService fileUpload;

	@Autowired
	private ProductService prodService;

	@Value("${category.image.path}")
	private String categoryImgPath;

	@PostMapping(path = "/")
	public ResponseEntity<CategoryDto> createCategory(@RequestBody CategoryDto catDto) {
		CategoryDto createdCategory = this.catService.createCategory(catDto);
		return new ResponseEntity<>(createdCategory, HttpStatus.CREATED);
	}

	@GetMapping(path = "/")
	public ResponseEntity<PageableResponse<CategoryDto>> getAllCategories(
			@RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
			@RequestParam(value = "pageSize", defaultValue = "5", required = false) int pageSize,
			@RequestParam(value = "sortDir", defaultValue = "a", required = false) String sortDir,
			@RequestParam(value = "sortBy", defaultValue = "title", required = false) String sortBy) {
		PageableResponse<CategoryDto> allCategories = this.catService.getAllCategories(pageNo, pageSize, sortDir,
				sortBy);
		return new ResponseEntity<>(allCategories, HttpStatus.OK);
	}

	@GetMapping(path = "/{catId}")
	public ResponseEntity<CategoryDto> getCategoryById(@PathVariable String catId) {
		CategoryDto categoryById = this.catService.getCategoryById(catId);
		return new ResponseEntity<>(categoryById, HttpStatus.OK);
	}

	@DeleteMapping(path = "/{catId}")
	public ResponseEntity<ApiResponse> deleteCategory(@PathVariable String catId) throws IOException {
		this.catService.deleteCategory(catId);
		ApiResponse response = ApiResponse.builder().message("Category with id: " + catId + " deleted").success(true)
				.status(HttpStatus.OK).build();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PatchMapping(path = "/{catId}")
	public ResponseEntity<CategoryDto> updateCategory(@RequestBody CategoryDto catDto, @PathVariable String catId) {
		CategoryDto updatedCategory = this.catService.updateCategory(catDto, catId);
		return new ResponseEntity<>(updatedCategory, HttpStatus.OK);
	}

	@PostMapping(path = "/image/{catId}")
	public ResponseEntity<ImageResponse> uploadCatImg(@RequestParam MultipartFile catImg, @PathVariable String catId)
			throws IOException {
		CategoryDto categoryById = this.catService.getCategoryById(catId);
		String uploadedImg = this.fileUpload.imageUpload(catImg, categoryImgPath);
		if (categoryById.getCat_img().isEmpty()) {
			categoryById.setCat_img(uploadedImg);
		} else if (categoryById.getCat_img().equals("pic.png")) {
			categoryById.setCat_img(uploadedImg);
		} else {
			Files.delete(Paths.get(categoryImgPath + categoryById.getCat_img()));
			categoryById.setCat_img(uploadedImg);
		}
		this.catService.updateCategory(categoryById, catId);
		ImageResponse response = ImageResponse.builder().imageName(uploadedImg)
				.message("Category image uploaded successfully").success(true).status(HttpStatus.OK).build();

		return new ResponseEntity<ImageResponse>(response, HttpStatus.OK);
	}

	@GetMapping(path = "/image/{catId}")
	public void categoryImage(@PathVariable String catId, HttpServletResponse response) throws IOException {
		CategoryDto categoryById = this.catService.getCategoryById(catId);
		InputStream catImg = this.fileUpload.getResource(categoryImgPath, categoryById.getCat_img());
		response.setContentType(MediaType.IMAGE_JPEG_VALUE);
		StreamUtils.copy(catImg, response.getOutputStream());
	}

	@PostMapping(path = "/{categoryId}/products")
	public ResponseEntity<ProductDto> createProductWithCategory(@RequestBody ProductDto prodDto,
			@PathVariable String categoryId) {

		ProductDto prodDtoWithCategory = this.prodService.createWithCategory(prodDto, categoryId);
		return new ResponseEntity<>(prodDtoWithCategory, HttpStatus.CREATED);
	}

	@PostMapping(path = "/{categoryId}/products/{productId}")
	public ResponseEntity<ProductDto> assignCategory(@PathVariable String categoryId, @PathVariable String productId) {
		ProductDto productDto = this.prodService.assignCategory(categoryId, productId);
		return new ResponseEntity<>(productDto, HttpStatus.OK);
	}
}
