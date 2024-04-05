package com.tech.haven.services.implementations;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.tech.haven.dtos.CategoryDto;
import com.tech.haven.exceptions.ResourceNotFoundException;
import com.tech.haven.helpers.PageToPageableHelper;
import com.tech.haven.helpers.PageableResponse;
import com.tech.haven.models.Category;
import com.tech.haven.repositories.CategoryRepository;
import com.tech.haven.services.CategoryService;

@Service
public class CategoryServiceImplementation implements CategoryService {

	@Autowired
	private CategoryRepository catRepo;

	@Autowired
	private ModelMapper modelMapper;

	@Value("${category.image.path}")
	private String catImgPath;

	@Override
	public CategoryDto createCategory(CategoryDto catDto) {

		Category cat = this.modelMapper.map(catDto, Category.class);
		cat.setCategory_id(UUID.randomUUID().toString());
		cat.setCat_img("pic.png");
		Category savedCategory = this.catRepo.save(cat);
		return this.modelMapper.map(savedCategory, CategoryDto.class);
	}

	@Override
	public CategoryDto getCategoryById(String catId) {

		Category cat = this.catRepo.findById(catId)
				.orElseThrow(() -> new ResourceNotFoundException("Category", "id", catId));
		CategoryDto catDto = this.modelMapper.map(cat, CategoryDto.class);
		return catDto;
	}

	@Override
	public PageableResponse<CategoryDto> getAllCategories(int pageNo, int pageSize, String sortDir, String sortBy) {

		Sort sort = (sortDir.trim().substring(0, 1).equalsIgnoreCase("d")) ? (Sort.by(sortBy).descending())
				: (Sort.by(sortBy).ascending());
		Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
		Page<Category> allCats = this.catRepo.findAll(pageable);

		PageableResponse<CategoryDto> catDto = PageToPageableHelper.getPageableResponse(allCats, CategoryDto.class);

		return catDto;
	}

	@Override
	public void deleteCategory(String catId) throws IOException {

		Category category = this.catRepo.findById(catId)
				.orElseThrow(() -> new ResourceNotFoundException("Category", "id", catId));
		Files.delete(Paths.get(catImgPath + category.getCat_img()));
		this.catRepo.delete(category);

	}

	@Override
	public CategoryDto updateCategory(CategoryDto categoryDto, String catId) {

		Category category = this.catRepo.findById(catId)
				.orElseThrow(() -> new ResourceNotFoundException("Category", "id", catId));

		category.setTitle(categoryDto.getTitle());
		category.setDescription(categoryDto.getDescription());
		category.setCat_img(categoryDto.getCat_img());
		Category savedCategory = this.catRepo.save(category);
		return this.modelMapper.map(savedCategory, CategoryDto.class);
	}

}
