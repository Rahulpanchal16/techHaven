package com.tech.haven.dtos;

import java.util.HashSet;
import java.util.Set;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
public class UserDto {

	private String user_id;

	@NotBlank(message = "Name cannot be blank")
	private String name;

	@Pattern(regexp = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$", message = "Invalid email address")
	@NotBlank(message = "Email cannot be blank")
	private String email;

	@Size(min = 6, message = "Password should be minimum of 6 characters")
	@NotBlank(message = "Password cannot be blank")
	private String password;

	private String gender;

	@Size(max = 500)
	private String about;

	private String image;

	@Builder.Default
	private Set<RoleDto> roles = new HashSet<>();

}
