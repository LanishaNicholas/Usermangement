package com.example.UserManagement.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.UserManagement.model.User;
import com.example.UserManagement.repository.UserRepository;
import com.example.UserManagement.security.*;

@Service
public class MyUserDetailsService implements UserDetailsService {
	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Optional<User> user = userRepository.findByEmail(email);
		if(user == null) {
			System.out.println("User not found");
			throw new UsernameNotFoundException("User not Found");
		}
		return new UserInfoDetails(user);
	}

	
}
