package com.example.UserManagement.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.UserManagement.model.User;
import com.example.UserManagement.service.UserService;

@RestController
@CrossOrigin(origins = {"*"},
		allowedHeaders = "Authorization")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@PostMapping("/register")
	public ResponseEntity<?> register(@RequestBody User user){
		Map<String, Object> response = new HashMap<>();
		Optional<User> userexists = userService.getUserProfile(user.getEmail());
		if(userexists.isEmpty()) {
			User user1 = userService.register(user);
			
			if(user1 != null) {
				response.put("status",200);
				response.put("message","Successfully Registered");
				return ResponseEntity.ok(response);
			}
			
			response.put("status", 500);
            response.put("message", "Something went wrong");
            return ResponseEntity.status(500).body(response);
		}
		
		response.put("status", 401);
        response.put("message", "User Already Exists");
        return ResponseEntity.status(401).body(response);
	}
	
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody User user){
		String token = userService.verify(user);
		Map<String, Object> response = new HashMap<>();
		response.put("status", 200);
        response.put("message", "Successfully Logged In");
        response.put("token", token);
        
        return ResponseEntity.ok(response);
	}
	
	@GetMapping("/getProfile")
	public ResponseEntity<?> getProfile(){
		var authentication = SecurityContextHolder.getContext().getAuthentication();
		System.out.println(authentication.getName());
		Optional<User> user = userService.getUserProfile(authentication.getName());
        if(user.isEmpty()){
            Map<String, Object> response = new HashMap<>();
            response.put("status", 404);
            response.put("message", "User Not Found");
            return ResponseEntity.status(404).body(response);
        }else{
            Map<String, Object> response = new HashMap<>();
            response.put("status", 200);
            response.put("data", user);
            return ResponseEntity.status(200).body(response);
        }
	}
	
	@PostMapping("/editProfile")
	public ResponseEntity<?> editProfile(@RequestBody User newData){
		var authentication = SecurityContextHolder.getContext().getAuthentication();
		var email = authentication.getName();
		
		if(!email.isEmpty()) {
			Optional<User> user = userService.getUserProfile(email);
			User newUser = user.get();
			newUser.setFullname(newData.getFullname());
			newUser.setPhonenumber(newData.getPhonenumber());
			newUser.setUsername(newData.getUsername());
			
			userService.register(newUser);
			return ResponseEntity.status(200).body("Succesfully edited");
		}
		
		return ResponseEntity.status(404).body("User not found");
		
		
	}
	
	@GetMapping("/getAllUsers")
	public ResponseEntity<List<User>> getAllUsers(){
		//System.out.println("enter getallusers");
		List<User> users = userService.getAllUsers();
		return new ResponseEntity<>(users, HttpStatus.OK);
	}
	
	// Endpoint to get a user by email
    @GetMapping("/getUserByEmail")
    public Optional<User> getUserByEmail(@RequestParam String email) {
    	return  userService.getUserProfile(email);
    	
                
    }

}
