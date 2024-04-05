package com.tech.haven.dtos;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

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

public class CartDto {

	private String cartId;

	@JsonFormat(pattern = "dd-MM-yyyy HH:mm")
	private LocalDateTime cartCreatedOn;

	private UserDto user;
	@Builder.Default
	private List<CartItemDto> items = new ArrayList<>();
}
