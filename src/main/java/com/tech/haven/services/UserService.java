package com.tech.haven.services;

import java.util.List;
import java.util.Optional;

import com.tech.haven.dtos.UserDto;
import com.tech.haven.helpers.PageableResponse;
import com.tech.haven.models.User;

import jakarta.mail.MessagingException;

public interface UserService {

	UserDto createUser(UserDto userDto) throws MessagingException, InterruptedException;

	UserDto updateUser(UserDto userDto, String user_id);

	void deleteUser(String user_id);

	PageableResponse<UserDto> getAllUsers(int pageNumber, int pageSize, String sortBy, String sortDir);

	UserDto getUserById(String user_id);

	UserDto getUserByUsername(String username);

	UserDto getUserByEmail(String email);

	List<UserDto> searchUsers(String keyword);

	List<UserDto> getUsersByGender(String gender);

	Optional<User> getUserByEmailGoogleAuth(String email);

}
