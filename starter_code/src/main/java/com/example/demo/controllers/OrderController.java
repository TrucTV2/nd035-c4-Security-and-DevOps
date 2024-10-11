package com.example.demo.controllers;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;

@RestController
@RequestMapping("/api/order")
public class OrderController {
	
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private OrderRepository orderRepository;

	private static final Logger logger = LogManager.getLogger(OrderController.class);
	
	
	@PostMapping("/submit/{username}")
	public ResponseEntity<UserOrder> submit(@PathVariable String username) {
		logger.info("Attempting usename create order : {}", username);
		User user = userRepository.findByUsername(username);
		if(user == null) {
			String msg = "Order create failed: username not found.";
			logger.info(msg);
			throw new UsernameNotFoundException(msg);
		}
		UserOrder order = UserOrder.createFromCart(user.getCart());
		orderRepository.save(order);
		logger.info("Success: Order created. id : {}", order.getId());
		return ResponseEntity.ok(order);
	}
	
	@GetMapping("/history/{username}")
	public ResponseEntity<List<UserOrder>> getOrdersForUser(@PathVariable String username) {
		logger.info("Attempting usename get order : {}", username);
		User user = userRepository.findByUsername(username);
		if(user == null) {
			String msg = "getOrdersForUser failed: username not found.";
			logger.info(msg);
			throw new UsernameNotFoundException(msg);
		}
		List<UserOrder> orders = orderRepository.findByUser(user);
		if (orders.isEmpty()) {
			logger.info("getOrdersForUser - No orders found. {}", user);
		} else {
			logger.info("getOrdersForUser  Success: orders found.", orders);
		}
		return ResponseEntity.ok(orders);
	}
}
