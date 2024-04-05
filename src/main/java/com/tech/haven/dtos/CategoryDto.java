package com.tech.haven.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryDto {

	private String category_id;
	@NotBlank(message = "title cannot be blank")
	@Size(min = 3, max = 50, message = "title must be between 3 and 50 characters long")
	private String title;
	private String description;
	private String cat_img;
}
