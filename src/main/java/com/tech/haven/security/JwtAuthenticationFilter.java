package com.tech.haven.security;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	@Autowired
	private Logger logger = LoggerFactory.getLogger(OncePerRequestFilter.class);

	@Autowired
	private JwtAltHelper jwtHelper;

	@Autowired
	private UserDetailsService userDetailsService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String requestHeader = request.getHeader("Authorization");
		logger.info("Header: {}", requestHeader);

		String username = null;
		String token = null;

		if (requestHeader != null && requestHeader.startsWith("Bearer ")) {
			// we can move forward
			token = requestHeader.substring(7);
			try {
				username = jwtHelper.extractUsername(token);
			} catch (IllegalArgumentException e) {
				// TODO: handle exception
				logger.warn("Illegar Argument while fetching the username");
				e.printStackTrace();
			} catch (ExpiredJwtException e) {
				// TODO: handle exception
				logger.warn("Jwt expired");
				e.printStackTrace();
			} catch (MalformedJwtException e) {
				// TODO: handle exception
				logger.warn("The Jwt has been changed or malformed");
				e.printStackTrace();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}

		} else {
			logger.warn("Header empty");
		}

		if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			// fetch user details from username
			UserDetails userByUsername = this.userDetailsService.loadUserByUsername(username);
			Boolean validateToken = this.jwtHelper.validateToken(token, userByUsername);	
			if (validateToken) {
				// set Authentication now
				UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userByUsername, null,
						userByUsername.getAuthorities());
				auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(auth);
			} else {
				logger.warn("Validation failed for user");
			}
		}
		filterChain.doFilter(request, response);
	}

}
