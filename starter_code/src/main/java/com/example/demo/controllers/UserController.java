package com.example.demo.controllers;

import java.util.List;
import java.util.Optional;

import com.example.demo.util.UsernameExistsException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;

@RestController
@RequestMapping("/api/user")
public class UserController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	private static final Logger logger = LogManager.getLogger(UserController.class);

	@GetMapping("/id/{id}")
	public ResponseEntity<User> findById(@PathVariable Long id) {
		logger.info("findById - Attempting to find user with id: {}", id);
		Optional<User> userList = userRepository.findById(id);
		if(!userList.isPresent()) {
			logger.info("Invalid user id = {}", id.toString());
			return ResponseEntity.notFound().build();
		}
		logger.info("Fetched user from service in controller: {}", userList);

		return ResponseEntity.of(userList);
	}
	
	@GetMapping("/{username}")
	public ResponseEntity<User> findByUserName(@PathVariable String username) {
		logger.info("FindByUserName - Attempting to find user with username: {}", username);
		User user = userRepository.findByUsername(username);
		if (user == null) {
			logger.info("Invalid user name = {}", username);
			return ResponseEntity.notFound().build();
		}
		logger.info("Fetched user from service in controller: {} : {}", user);
		return ResponseEntity.ok(user);
	}
	
	@PostMapping("/create")
	public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) {
		logger.info("createUser - Attempting to find user create: {}", createUserRequest);

		User exists = userRepository.findByUsername(createUserRequest.getUsername());
		if (exists != null) {
			String userExistsErrorMessage = "User create failed. Reason: Username already exists.";
			logger.info(userExistsErrorMessage);
			throw new UsernameNotFoundException(userExistsErrorMessage);
		}


		User user = new User();
		user.setUsername(createUserRequest.getUsername());
		Cart cart = new Cart();
		cartRepository.save(cart);
		user.setCart(cart);
		if(createUserRequest.getPassword().length()<7 ||
				!createUserRequest.getPassword().equals(createUserRequest.getConfirmPassword())){
			// Either length is less than 7 or pass and conf pass do not match.
			final ResponseEntity<User> response = ResponseEntity.badRequest().build();
			logger.info("Create User Failure", response);
			return response;

		}
		user.setPassword(bCryptPasswordEncoder.encode(createUserRequest.getPassword()));
		userRepository.save(user);
		final ResponseEntity<User> response = ResponseEntity.ok(user);
		logger.info("Create User Success. User Name : {}", user.getUsername());
		return response;
	}
	
}
