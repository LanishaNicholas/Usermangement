package com.example.UserManagement.security;

import java.util.Collection;
import java.util.Optional;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.UserManagement.model.User;


public class UserInfoDetails implements UserDetails {
	
	private final String email;
	private final String password;
	
	
	/*public UserInfoDetails(User user) {
		email = user.getEmail();
		password = user.getPassword();
	}*/
	
	public UserInfoDetails(Optional<User> optionalUser) {
		if(optionalUser.isPresent()) {
			User user = optionalUser.get();
			email = user.getEmail();
			password = user.getPassword();
		}else {
			email = null;
			password = null;
		}
	}
	
	

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		
		return null;
	}

	@Override
	public String getPassword() {
		
		return password;
	}

	@Override
	public String getUsername() {
		
		return email;
	}
	
	@Override
	public boolean isAccountNonExpired() {
		
		return true;
	}
	
	@Override
	public boolean isAccountNonLocked() {
		
		return true;
	}

	@Override
	public boolean isEnabled() {
		
		return true;
	}
	
	@Override
	public boolean isCredentialsNonExpired() {
		
		return true;
	}
}
