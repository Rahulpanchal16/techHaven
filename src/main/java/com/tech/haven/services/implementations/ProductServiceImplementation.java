package com.tech.haven.services.implementations;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.tech.haven.dtos.ProductDto;
import com.tech.haven.exceptions.ResourceNotFoundException;
import com.tech.haven.helpers.PageToPageableHelper;
import com.tech.haven.helpers.PageableResponse;
import com.tech.haven.models.Category;
import com.tech.haven.models.Product;
import com.tech.haven.repositories.CategoryRepository;
import com.tech.haven.repositories.ProductRepository;
import com.tech.haven.services.ProductService;

@Service
public class ProductServiceImplementation implements ProductService {

	@Autowired
	private static final Logger logger = org.slf4j.LoggerFactory.getLogger(ProductServiceImplementation.class);

	@Autowired
	private ProductRepository prodRepo;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private CategoryRepository catRepo;

	@Value("${product.images.path}")
	private String productPath;

	@Override
	public ProductDto createProduct(ProductDto productDto) {
		Product product = this.modelMapper.map(productDto, Product.class);
		product.setProduct_id(UUID.randomUUID().toString());
		product.setDateAdded(new Date());
		product.setImage("defaultProductImage.png");
		Product savedProduct = this.prodRepo.save(product);
		return this.modelMapper.map(savedProduct, ProductDto.class);
	}

	@Override
	public ProductDto getProductById(String prodId) {
		Product productById = this.prodRepo.findById(prodId)
				.orElseThrow(() -> new ResourceNotFoundException("Product", "product title", prodId));
		ProductDto productDto = this.modelMapper.map(productById, ProductDto.class);
		return productDto;
	}

	@Override
	public PageableResponse<ProductDto> getAllProducts(int pageNumber, int pageSize, String sortBy, String sortDir) {
		Sort sort = (sortDir.trim().substring(0, 1).equalsIgnoreCase("d")) ? Sort.by((sortBy)).descending()
				: Sort.by((sortBy)).ascending();
		Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
		Page<Product> allProducts = this.prodRepo.findAll(pageable);
		PageableResponse<ProductDto> allProductsDto = PageToPageableHelper.getPageableResponse(allProducts,
				ProductDto.class);
		return allProductsDto;
	}

	@Override
	public PageableResponse<ProductDto> getProductsByTitle(String prodTitle, int pageNumber, int pageSize,
			String sortBy, String sortDir) {
		Sort sort = (sortDir.trim().substring(0, 1).equalsIgnoreCase("d")) ? Sort.by((sortBy)).descending()
				: Sort.by((sortBy)).ascending();
		Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
		Page<Product> page = this.prodRepo.findByTitleContaining(prodTitle, pageable);
		PageableResponse<ProductDto> productDtosByTitle = PageToPageableHelper.getPageableResponse(page,
				ProductDto.class);
		return productDtosByTitle;
	}

	@Override
	public PageableResponse<ProductDto> getProductsByLiveStatus(Boolean isLive, int pageNumber, int pageSize,
			String sortBy, String sortDir) {
		Sort sort = (sortDir.trim().substring(0, 1).equalsIgnoreCase("d")) ? Sort.by((sortBy)).descending()
				: Sort.by((sortBy)).ascending();
		Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
		Page<Product> isLivePage = this.prodRepo.findByIsLive(isLive, pageable);
		PageableResponse<ProductDto> productDtosByLiveStatus = PageToPageableHelper.getPageableResponse(isLivePage,
				ProductDto.class);
		return productDtosByLiveStatus;
	}

	@Override
	public ProductDto updateProduct(ProductDto prodDto, String prodId) {
		Product product = this.prodRepo.findById(prodId)
				.orElseThrow(() -> new ResourceNotFoundException("Product", "prodId", prodId));
		product.setTitle(prodDto.getTitle());
		product.setDescription(prodDto.getDescription());
		product.setPrice(prodDto.getPrice());
		product.setQuantity(prodDto.getQuantity());
		product.setDiscount(prodDto.getDiscount());
		product.setDateAdded(new Date());
		product.setIsLive(prodDto.getIsLive());
		product.setInStock(prodDto.getInStock());
		product.setImage(prodDto.getImage());
		Product updatedProd = this.prodRepo.save(product);
		ProductDto updatedProductDto = this.modelMapper.map(updatedProd, ProductDto.class);
		return updatedProductDto;

	}

	@Override
	public void deleteProduct(String prodId) throws IOException {
		Product product = this.prodRepo.findById(prodId)
				.orElseThrow(() -> new ResourceNotFoundException("Product", "id", prodId));
		String productImageName = product.getImage();
		Path path = Paths.get(productPath + productImageName);
		if (Files.exists(path)) {
			Files.delete(path);
			this.prodRepo.delete(product);
		} else {
			this.prodRepo.delete(product);
			logger.info("No image for this product found, deleting the data");
		}
	}

	@Override
	public PageableResponse<ProductDto> getProductsByStock(Boolean inStock, int pageNumber, int pageSize, String sortBy,
			String sortDir) {
		Sort sort = (sortDir.trim().substring(0, 1).equalsIgnoreCase("d")) ? Sort.by((sortBy)).descending()
				: Sort.by((sortBy)).ascending();
		Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
		Page<Product> productPageByStock = this.prodRepo.findByInStock(inStock, pageable);
		PageableResponse<ProductDto> pageableResponse = PageToPageableHelper.getPageableResponse(productPageByStock,
				ProductDto.class);
		return pageableResponse;

	}

	@Override
	public ProductDto createWithCategory(ProductDto prodDto, String categoryId) {

		Category category = this.catRepo.findById(categoryId)
				.orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));
		Product product = this.modelMapper.map(prodDto, Product.class);
		product.setProduct_id(UUID.randomUUID().toString());
		product.setDateAdded(new Date());
		product.setImage("defaultProductImage.png");
		product.setCategory(category);
		Product savedProduct = this.prodRepo.save(product);
		return this.modelMapper.map(savedProduct, ProductDto.class);
	}

	@Override
	public ProductDto assignCategory(String categoryId, String prodId) {
		Product product = this.prodRepo.findById(prodId)
				.orElseThrow(() -> new ResourceNotFoundException("Product", "product id", prodId));
		Category category = this.catRepo.findById(categoryId)
				.orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));
		product.setCategory(category);
		Product updatedProduct = this.prodRepo.save(product);
		ProductDto prodDto = this.modelMapper.map(updatedProduct, ProductDto.class);
		return prodDto;
	}

	@Override
	public PageableResponse<ProductDto> findByCategory(String categoryId, int pageNumber, int pageSize, String sortBy,
			String sortDir) {
		Category category = this.catRepo.findById(categoryId)
				.orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));
		Sort sort = sortDir.trim().substring(0, 1).equalsIgnoreCase("d") ? Sort.by(sortBy).descending()
				: Sort.by(sortBy).ascending();
		Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
		Page<Product> productPage = this.prodRepo.findByCategory(category, pageable);
		PageableResponse<ProductDto> productDto = PageToPageableHelper.getPageableResponse(productPage,
				ProductDto.class);
		return productDto;
	}

}