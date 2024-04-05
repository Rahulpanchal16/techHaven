package com.tech.haven.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.tech.haven.dtos.RoleDto;
import com.tech.haven.dtos.UserDto;
import com.tech.haven.helpers.ApiResponse;
import com.tech.haven.helpers.ImageResponse;
import com.tech.haven.helpers.PageableResponse;
import com.tech.haven.security.JwtAltHelper;
import com.tech.haven.services.FileUploadService;
import com.tech.haven.services.UserService;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping(path = "/api/users")
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private FileUploadService fileUploadService;

	@Value("${user.profile.image.path}")
	private String imgUploadPath;

	@Autowired
	private final Logger logger = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private JwtAltHelper helper;

	// create user
	@PostMapping(path = "/")
	public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto userDto)
			throws MessagingException, InterruptedException {
		UserDto createdUser = this.userService.createUser(userDto);
		return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
	}

	// get all users
	@GetMapping(path = "/")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<PageableResponse<UserDto>> getUsers(
			@RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
			@RequestParam(value = "pageSize", defaultValue = "5", required = false) int pageSize,
			@RequestParam(value = "sortBy", defaultValue = "name", required = false) String sortBy,
			@RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir) {
		PageableResponse<UserDto> allUsers = this.userService.getAllUsers(pageNumber, pageSize, sortBy, sortDir);
		return new ResponseEntity<>(allUsers, HttpStatus.OK);
	}

	// get user by id
	@GetMapping(path = "/{userId}", headers = "Authorization")
	public ResponseEntity<?> getUserById(@PathVariable String userId,
			@RequestHeader(name = "Authorization") String AuthHeader) {
		logger.info(AuthHeader);
		String usernameFromToken = helper.extractUsername(AuthHeader.substring(7));
		logger.info(usernameFromToken);
		UserDto userByToken = this.userService.getUserByEmail(usernameFromToken);
		Set<RoleDto> roles = userByToken.getRoles();
		UserDto userById = this.userService.getUserById(userId);
		if (userByToken.getUser_id().equals(userId)
				|| roles.stream().anyMatch(role -> role.getRoleName().equals("ROLE_ADMIN"))) {
			return new ResponseEntity<>(userById, HttpStatus.OK);
		} else {
			ApiResponse response = ApiResponse.builder().message("Bad Request").success(false)
					.status(HttpStatus.FORBIDDEN).build();
			return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
		}
		// return new ResponseEntity<>(userById, HttpStatus.OK);
	}

	// delete user by id
	@DeleteMapping(path = "/{userId}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponse> deleteUser(@PathVariable String userId) {
		this.userService.deleteUser(userId);
		ApiResponse message = ApiResponse.builder().message("User with id: " + userId + " deleted").success(true)
				.status(HttpStatus.OK).build();
		return new ResponseEntity<>(message, HttpStatus.OK);
	}

	// get user by email
	@GetMapping(path = "/email/{email}")
	public ResponseEntity<UserDto> getUserByEmail(@PathVariable String email) {
		UserDto userByEmail = this.userService.getUserByEmail(email);
		return new ResponseEntity<>(userByEmail, HttpStatus.OK);
	}

	// search user by keyword
	@GetMapping(path = "/search/{keyword}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<List<UserDto>> searchUser(@PathVariable String keyword) {
		List<UserDto> searchedUsers = this.userService.searchUsers(keyword);
		return new ResponseEntity<>(searchedUsers, HttpStatus.OK);
	}

	// upload user image api

	@PostMapping(path = "/image/{userId}")
	public ResponseEntity<ImageResponse> uploadUserImage(@RequestParam("userImg") MultipartFile userImg,
			@PathVariable String userId) throws IOException {
		String fullFileName = fileUploadService.imageUpload(userImg, imgUploadPath);

		UserDto userDtoById = this.userService.getUserById(userId);

		if (userDtoById.getImage().isEmpty()) {
			userDtoById.setImage(fullFileName);
		} else if (userDtoById.getImage().equals("pic.png")) {
			userDtoById.setImage(fullFileName);
		} else {
			Files.delete(Paths.get(imgUploadPath + userDtoById.getImage()));
			userDtoById.setImage(fullFileName);
		}
		this.userService.updateUser(userDtoById, userId);

		ImageResponse response = ImageResponse.builder().imageName(fullFileName)
				.message("User image uploaded successfully").success(true).status(HttpStatus.CREATED).build();

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	// serve user image
	@GetMapping(path = "/image/{userId}")
	public void serveUserImage(@PathVariable String userId, HttpServletResponse response) throws IOException {
		UserDto userById = this.userService.getUserById(userId);
		InputStream resource = this.fileUploadService.getResource(imgUploadPath, userById.getImage());
		response.setContentType(MediaType.IMAGE_JPEG_VALUE);
		StreamUtils.copy(resource, response.getOutputStream());
	}

}
