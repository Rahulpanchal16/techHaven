package com.tech.haven.controllers;

import java.io.IOException;
import java.security.Principal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.tech.haven.dtos.UserDto;
import com.tech.haven.exceptions.BadApiRequestException;
import com.tech.haven.helpers.JwtRequest;
import com.tech.haven.helpers.JwtResponse;
import com.tech.haven.models.User;
import com.tech.haven.security.JwtAltHelper;
import com.tech.haven.services.UserService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;

@RestController
@RequestMapping(path = "/api/auth")
@Tag(name = "Authentication & Authorization Controller", description = "REST endpoints for authentication and authorization")
// @CrossOrigin("*")
public class AuthController {

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private UserService userService;

	private Logger logger = LoggerFactory.getLogger(AuthController.class);

	// @Autowired
	// private UserService userService;

	@Autowired
	private JwtAltHelper jwtHelper;

	@Value("${google.client.id}")
	private String googleClientId;

	@Value("${google.user.password}")
	private String newPassword;

	private void authenticate(String email, String password) {
		// TODO Auto-generated method stub
		UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(email, password);
		try {
			authenticationManager.authenticate(auth);
		} catch (BadCredentialsException e) {
			// TODO: handle exception
			throw new BadApiRequestException("Invalid username or password");
		}

	}

	@PostMapping(path = "/login")
	public ResponseEntity<JwtResponse> login(@RequestBody JwtRequest jwtRequest) {
		this.authenticate(jwtRequest.getEmail(), jwtRequest.getPassword());

		UserDetails userDetails = this.userDetailsService.loadUserByUsername(jwtRequest.getEmail());

		String jwtToken = this.jwtHelper.GenerateToken(userDetails.getUsername());

		UserDto userDto = this.modelMapper.map(userDetails, UserDto.class);

		JwtResponse response = JwtResponse.builder().jwtToken(jwtToken).user(userDto).build();

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping(path = "/logged-in-user")
	public ResponseEntity<UserDetails> getLoggedInUser(Principal principal) {

		String loggedInUsername = principal.getName().toString();

		return new ResponseEntity<>(userDetailsService.loadUserByUsername(loggedInUsername), HttpStatus.OK);
	}

	@PostMapping(path = "/google-auth")
	public ResponseEntity<JwtResponse> loginWithGoogle(@RequestBody Map<String, Object> data)
			throws IOException, MessagingException, InterruptedException {
		// get the Id Token from the request
		String idToken = data.get("idToken").toString();
		NetHttpTransport netHttpTransport = new NetHttpTransport();
		GsonFactory jacksonFactory = GsonFactory.getDefaultInstance();
		GoogleIdTokenVerifier.Builder verifier = new GoogleIdTokenVerifier.Builder(netHttpTransport, jacksonFactory)
				.setAudience(Collections.singleton(googleClientId));

		GoogleIdToken googleIdToken = GoogleIdToken.parse(verifier.getJsonFactory(), idToken);

		GoogleIdToken.Payload payload = googleIdToken.getPayload();

		logger.info("Payload: {} ", payload);

		String email = payload.getEmail();

		User user = null;
		user = this.userService.getUserByEmailGoogleAuth(email).orElse(null);
		if (user == null) {
			user = this.saveUser(email, data.get("name").toString(), data.get("photoUrl").toString());
		}
		JwtRequest request = JwtRequest.builder().email(email).password(newPassword).build();
		ResponseEntity<JwtResponse> responseEntity = this.login(request);
		return responseEntity;
	}

	private User saveUser(String email, String name, String photoUrl) throws MessagingException, InterruptedException {
		UserDto userDto = UserDto.builder().name(name).email(email).password(newPassword).roles(new HashSet<>())
				.image(photoUrl).build();
		UserDto createdUser = this.userService.createUser(userDto);
		return this.modelMapper.map(createdUser, User.class);
	}

}
