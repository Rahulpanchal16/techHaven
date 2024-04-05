package com.tech.haven.helpers;

import org.springframework.http.HttpStatus;

import com.tech.haven.dtos.CartDto;

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
public class CartDtoResponse {

	private CartDto cartDto;
	private String message;
	private boolean success;
	private HttpStatus status;

}
