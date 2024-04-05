package com.tech.haven.configurations;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.tech.haven.security.JwtAuthenticationEntryPoint;
import com.tech.haven.security.JwtAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@EnableWebSecurity
public class CustomSecurityConfigurations {

	private final String[] PUBLIC_URLS = { "/api/auth/login", "/api/auth/google-auth", "/swagger-ui/**", "/webjars/**",
			"/swagger-resources/**", "/v3/**" ,"/swagger-ui.html","/api/gzip/**"};

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private JwtAuthenticationEntryPoint authEntryPoint;

	@Autowired
	private JwtAuthenticationFilter authFilter;

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
		return authConfig.getAuthenticationManager();
	}

	// CORS Configurations 2 (recommended)
	@Bean
	CorsConfigurationSource corsConfig() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-type", "Accept"));
		configuration.setAllowCredentials(true);
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE"));
		configuration.addAllowedOriginPattern("*");
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

	// THIS IS THE CONFIGURATIONS FOR IN MEMORY AUTH
	// @Bean
	// UserDetailsService userDetailsService() {
	//
	// UserDetails normalUser =
	// User.builder().username("root").password(passwordEncoder().encode("root"))
	// .roles("NORMAL").build();
	//
	// UserDetails adminUser =
	// User.builder().username("admin").password(passwordEncoder().encode("admin"))
	// .roles("ADMIN").build();
	//
	// return new InMemoryUserDetailsManager(normalUser, adminUser);
	// }

	@Bean
	DaoAuthenticationProvider authProvider() {
		DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
		daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
		daoAuthenticationProvider.setUserDetailsService(this.userDetailsService);
		return daoAuthenticationProvider;
	}

	// SETTING UP CUSTOM FORM LOGIN USING SPRING SECURITY
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
		httpSecurity.csrf(csrf -> csrf.disable()).cors(cors -> cors.configurationSource(corsConfig()))
				.authorizeHttpRequests(auth -> auth.requestMatchers(PUBLIC_URLS).permitAll()
						.requestMatchers(HttpMethod.POST, "/api/users/**").permitAll()
						.requestMatchers(HttpMethod.GET, "/api/users/").hasRole("ADMIN")
						.requestMatchers(HttpMethod.DELETE, "/api/users/**").hasRole("ADMIN").anyRequest()
						.authenticated())
				.exceptionHandling(ex -> ex.authenticationEntryPoint(authEntryPoint)).sessionManagement(
						sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		// THIS IS COMMENTED BECAUSE JWT AUTH IS IMPLEMENTED AND BASIC AUTH IS NO LONGER
		// REQUIRED
//                .httpBasic(Customizer.withDefaults());
//                .formLogin(formLogin -> formLogin.loginPage("/login.html").loginProcessingUrl("/process-url").defaultSuccessUrl("/api/auth/logged-in-user").failureUrl("/error"))
//                .logout(logout -> logout.logoutUrl("/logout"));

		httpSecurity.addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class);

		return httpSecurity.build();
	}

	// TWO WAYS TO CONFIGURE CORS
	// CORS Configurations 1
//	@Bean
//	FilterRegistrationBean<CorsFilter> corsFilter() {
//		CorsConfiguration corsConfiguration = new CorsConfiguration();
//		corsConfiguration.addAllowedHeader("Authorization");
//		corsConfiguration.addAllowedHeader("Content-type");
//		corsConfiguration.addAllowedHeader("Accept");
//		corsConfiguration.setAllowCredentials(true);
//		corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PATCH", "PUT", "DELETE"));
//		corsConfiguration.addAllowedOriginPattern("*");
//		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//		source.registerCorsConfiguration("/**", corsConfiguration);
//		FilterRegistrationBean<CorsFilter> registrationBean = new FilterRegistrationBean<CorsFilter>(
//				new CorsFilter(source));
//		registrationBean.setOrder(-100);
//		return registrationBean;
//
//	}

}
