package com.tech.haven.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class OpenAPIDocConfig {

	@Bean
	OpenAPI customizeOpenApi() {

		final String securityScheme = "bearerAuth";

		OpenAPI openAPI = new OpenAPI();

		openAPI.info(new Info().title("Tech Haven - India's largest electronic store")
				.description("Backend REST APIs for Tech Haven")
				.contact(new Contact().email("panchalrahul180@gmail.com").name("Rahul Panchal")).version("V1.0.0")
				.license(new License().name("tech Haven").url("https://springdoc.org/migrating-from-springfox.html")
						.url("www.tech-haven.com")));

		openAPI.addSecurityItem(new SecurityRequirement().addList(securityScheme))
				.components(new Components().addSecuritySchemes(securityScheme, new SecurityScheme()
						.name(securityScheme).type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")));

		return openAPI;
	}

}
