package com.tech.haven.exceptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import com.tech.haven.helpers.ApiResponse;

import io.jsonwebtoken.ExpiredJwtException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ApiResponse> resourceNotFoundException(ResourceNotFoundException ex) {
		String message = ex.getMessage();
		ApiResponse apiResponse = ApiResponse.builder().message(message).success(false).status(HttpStatus.NOT_FOUND)
				.build();
		return new ResponseEntity<ApiResponse>(apiResponse, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, Object>> methodArgumentNotValidExceptionHandler(
			MethodArgumentNotValidException ex) {
		List<ObjectError> allErrors = ex.getBindingResult().getAllErrors();
		Map<String, Object> response = new HashMap<>();
		allErrors.stream().forEach((error) -> {
			String message = error.getDefaultMessage();
			String field = ((FieldError) error).getField();
			response.put(field, message);
		});
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(PropertyReferenceException.class)
	public ResponseEntity<ApiResponse> propertyReferenceExceptionHandler(PropertyReferenceException ex) {
		String message = ex.getMessage();
		ApiResponse response = ApiResponse.builder().message(message).success(false).status(HttpStatus.NOT_FOUND)
				.build();
		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(BadApiRequestException.class)
	public ResponseEntity<ApiResponse> badApiRequestExceptionHandler(BadApiRequestException ex) {
		String message = ex.getMessage();
		logger.info("bad request");
		ApiResponse response = ApiResponse.builder().message(message).success(false).status(HttpStatus.BAD_REQUEST)
				.build();
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	// MaxUploadSizeExceededException
	@ExceptionHandler(MaxUploadSizeExceededException.class)
	public ResponseEntity<ApiResponse> maxUploadSizeExceededExceptionHandler(MaxUploadSizeExceededException ex) {
		String message = ex.getMessage();
		ApiResponse response = ApiResponse.builder().message(message).success(false).status(HttpStatus.BAD_REQUEST)
				.build();
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(ExpiredJwtException.class)
	public ResponseEntity<ApiResponse> expiredJwtHandler(ExpiredJwtException ex) {
		String message = ex.getMessage();
		ApiResponse response = ApiResponse.builder().message(message).success(false).status(HttpStatus.BAD_REQUEST)
				.build();
		return new ResponseEntity<>(response, HttpStatus.BAD_GATEWAY);
	}

}
