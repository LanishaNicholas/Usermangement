package com.example.UserManagement.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.UserManagement.model.User;
import com.example.UserManagement.repository.UserRepository;

@Service
public class UserService {
	
	@Autowired
	private JWTService jwtService;
	
	@Autowired
	private UserRepository repository;
	
	@Autowired
	AuthenticationManager authenticationManager;
	
	private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
	
	public User register(User user) {
		user.setPassword(encoder.encode(user.getPassword()));
		repository.save(user);
		return user;
	}
	
	public String verify(User user) {
		Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));
		if(authentication.isAuthenticated()) {
			return jwtService.generateToken(user.getEmail());
		}else {
			return "Failed to authenticate";
		}
	}
	
	public Optional<User> getUserProfile(String email){
		return repository.findByEmail(email);
	}
	
	public List<User> getAllUsers(){
		return repository.findAll();
	}
	
	

}
