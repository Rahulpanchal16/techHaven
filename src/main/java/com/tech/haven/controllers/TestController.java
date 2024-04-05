package com.tech.haven.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/home")
public class TestController {

	@GetMapping(path = "/test")
	public String test() {
		return "This is a test handler";
	}
}
