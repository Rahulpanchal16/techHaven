package com.tech.haven.services.implementations;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.tech.haven.dtos.UserDto;
import com.tech.haven.exceptions.ResourceNotFoundException;
import com.tech.haven.helpers.PageToPageableHelper;
import com.tech.haven.helpers.PageableResponse;
//import com.tech.haven.helpers.SendEmail;
import com.tech.haven.models.Role;
import com.tech.haven.models.User;
import com.tech.haven.repositories.RoleRepository;
import com.tech.haven.repositories.UserRepository;
import com.tech.haven.services.UserService;

import jakarta.mail.MessagingException;

@Service
public class UserServiceImplementation implements UserService {

	private Logger logger = LoggerFactory.getLogger(UserServiceImplementation.class);

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private RoleRepository roleRepo;

	@Value("${user.profile.image.path}")
	private String imgPath;

	@Value("${role.normal}")
	private String normalRoleId;

	@Autowired
	private PasswordEncoder passwordEncoder;

//	@Autowired
//	private SendEmail sendMail;

	@Override
	public UserDto createUser(UserDto userDto) throws MessagingException, InterruptedException {
		// creating a unique random id for the user before saving
		String random_user_id = UUID.randomUUID().toString();
		// String gender = userDto.getGender().trim().toLowerCase().substring(0, 1);
		Role role = this.roleRepo.findById(normalRoleId)
				.orElseThrow(() -> new ResourceNotFoundException("No Role found by that ID"));
		User createdUser = this.modelMapper.map(userDto, User.class);
		createdUser.setPassword(passwordEncoder.encode(userDto.getPassword()));
		createdUser.setUser_id(random_user_id);
		createdUser.setGender("m");
		createdUser.setImage(userDto.getImage() == null ? "pic.png" : userDto.getImage());
		createdUser.getRoles().add(role);

		User savedUser = this.userRepo.save(createdUser);
//		sendMail.sendSimpleEmail(userDto.getEmail(), "Welcome to Tech Haven", "Hello, " + userDto.getName() + "\n"
//				+ "Welcome to , India's largest Online electronic store\n your account is successfully created\nThankyou, TechHaven team");
		UserDto savedUserDto = this.modelMapper.map(savedUser, UserDto.class);
		return savedUserDto;
	}

	@Override
	public UserDto updateUser(UserDto userDto, String user_id) {

		User userById = this.userRepo.findById(user_id)
				.orElseThrow(() -> new ResourceNotFoundException("User", "id", user_id));
		userById.setName(userDto.getName());
		userById.setAbout(userDto.getAbout());
		userById.setPassword(userDto.getPassword());
		userById.setGender(userDto.getPassword());
		userById.setImage(userDto.getImage());

		User updatedUser = this.userRepo.save(userById);
		UserDto updatedUserDto = this.modelMapper.map(updatedUser, UserDto.class);
		return updatedUserDto;
	}

	@Override
	public void deleteUser(String user_id) {
		User user = this.userRepo.findById(user_id)
				.orElseThrow(() -> new ResourceNotFoundException("user", "user id", user_id));

		String fullImagePath = imgPath + user.getImage();
		try {
			Files.delete(Paths.get(fullImagePath));
		} catch (NoSuchFileException ne) {
			logger.info("User image does not exist");
			ne.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();

		}
		user.getRoles().clear();
		this.userRepo.delete(user);
	}

	@Override
	public PageableResponse<UserDto> getAllUsers(int pageNumber, int pageSize, String sortBy, String sortDir) {

		Sort sort = (sortDir.trim().substring(0, 1).equalsIgnoreCase("d")) ? (Sort.by(sortBy).descending())
				: (Sort.by(sortBy).ascending());
		Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
		Page<User> page = this.userRepo.findAll(pageable);

		PageableResponse<UserDto> pageableResponse = PageToPageableHelper.getPageableResponse(page, UserDto.class);
		return pageableResponse;
	}

	@Override
	public UserDto getUserById(String user_id) {
		User userById = this.userRepo.findById(user_id)
				.orElseThrow(() -> new ResourceNotFoundException("User", "user id", user_id));
		UserDto userDtoById = this.modelMapper.map(userById, UserDto.class);
		return userDtoById;
	}

	@Override
	public UserDto getUserByUsername(String username) {
		User userByUsername = this.userRepo.findByUsername(username)
				.orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
		UserDto userDto = this.modelMapper.map(userByUsername, UserDto.class);
		return userDto;
	}

	@Override
	public List<UserDto> searchUsers(String keyword) {
		List<User> searchedUsers = this.userRepo.findByNameContaining(keyword)
				.orElseThrow(() -> new ResourceNotFoundException("No users found", "keyword: ", keyword));
		if (searchedUsers.isEmpty()) {
			throw new ResourceNotFoundException("users", "keyword", keyword);
		}
		List<UserDto> searchedUsersDto = searchedUsers.stream().map((user) -> this.modelMapper.map(user, UserDto.class))
				.collect(Collectors.toList());
		return searchedUsersDto;

	}

	@Override
	public UserDto getUserByEmail(String email) {
		User userByEmail = this.userRepo.getUserByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("user", "email", email));
		UserDto userDtoByEmail = this.modelMapper.map(userByEmail, UserDto.class);
		return userDtoByEmail;
	}

	@Override
	public List<UserDto> getUsersByGender(String gender) {
		List<User> usersByGender = this.userRepo.findByGenderContaining(gender.trim().toLowerCase().substring(0, 1))
				.orElseThrow(() -> new ResourceNotFoundException("users", "gender", gender));
		if (usersByGender.isEmpty()) {
			throw new ResourceNotFoundException("users", "gender", gender);
		}
		List<UserDto> usersDtoByGender = usersByGender.stream()
				.map((user) -> this.modelMapper.map(usersByGender, UserDto.class)).collect(Collectors.toList());
		return usersDtoByGender;
	}

	@Override
	public Optional<User> getUserByEmailGoogleAuth(String email) {
		return this.userRepo.getUserByEmail(email);
	}

}
