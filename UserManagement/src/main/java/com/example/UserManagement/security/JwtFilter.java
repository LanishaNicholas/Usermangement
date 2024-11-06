package com.example.UserManagement.security;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.naming.AuthenticationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.UserManagement.service.JWTService;
import com.example.UserManagement.service.MyUserDetailsService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {
	
	@Autowired
	private JWTService jwtService;
	
	@Autowired
	ApplicationContext applicationContext;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
		response.setHeader("Access-Control-Max-Age", "10000");
		response.setHeader("Access-Control-Allow-Headers", "authorization, content-type, xsrf-token");
		response.addHeader("Access-Control-Expose-Headers", "xsrf-token");
		
		if ("OPTIONS".equals(request.getMethod())) {
		    response.setStatus(HttpServletResponse.SC_OK);
		}else{
		
			String authorization = request.getHeader("Authorization");
			String token = null;
			String username = null;
			
			if(authorization != null && authorization.startsWith("Bearer")) {
				System.out.println(authorization);
				token = authorization.substring(7);
				username = jwtService.extractUsername(token);
			}
			
			if(username !=null && SecurityContextHolder.getContext().getAuthentication() == null){
	            UserDetails userDetails = applicationContext.getBean(MyUserDetailsService.class).loadUserByUsername(username);
	            if(jwtService.validateToken(token,userDetails)){
	                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
	                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
	                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
	            }
	        }
	        filterChain.doFilter(request,response);
		}
		
	}
	
	@RestControllerAdvice
	class CustomAuthenticationExceptionHandler{
		 /*
	    this class is to handle the unauthorized entry which
	    means users requesting api without token.
	    */
		
		@ExceptionHandler(AuthenticationException.class)
		public ResponseEntity<?> handleAuthenticationException(AuthenticationException ex) {
			System.out.println(ex.getMessage());
	        if(!Objects.equals(ex.getMessage(), "Bad credentials")) {
	            Map<String, Object> response = new HashMap<>();
	            response.put("status", 401);
	            response.put("message", "Token is required or invalid");
	            return ResponseEntity.status(401)
	                    .body(response);
	        }else{
	            Map<String, Object> response = new HashMap<>();
	            response.put("status", 401);
	            response.put("message", "Invalid Password");
	            return ResponseEntity.status(401)
	                    .body(response);
	        }
		}
	}

}
