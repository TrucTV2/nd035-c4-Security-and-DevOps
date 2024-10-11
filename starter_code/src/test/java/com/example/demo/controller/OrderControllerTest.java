package com.example.demo.controller;
import com.example.demo.TestUtils;
import com.example.demo.controllers.OrderController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {
    private OrderController orderController;

    private OrderRepository orderRepository = mock(OrderRepository.class);
    private UserRepository userRepository = mock(UserRepository.class);

    @Before
    public void setup(){
        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "orderRepository", orderRepository);
        TestUtils.injectObjects(orderController, "userRepository", userRepository);
    }

    @Test
    public void submitHappyPath(){
        User user = new User();
        Cart cart = new Cart();
        cart.setId(1L);
        cart.setUser(user);
        cart.setItems(Collections.emptyList());
        user.setCart(cart);
        user.setUsername("asd");
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        UserOrder userOrder = new UserOrder();
        userOrder.setUser(user);
        ResponseEntity<UserOrder> response = orderController.submit(user.getUsername());
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

    }

    @Test(expected = UsernameNotFoundException.class)
    public void submitSadPath(){
        when(userRepository.findByUsername("username")).thenReturn(null);
        orderController.submit("username");
    }

    @Test
    public void getOrdersForUserHappyPath(){
        User user = new User();
        user.setUsername("asd");
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        UserOrder userOrder = new UserOrder();
        userOrder.setUser(user);
        userOrder.setItems(Collections.emptyList());
        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser(user.getUsername());
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test(expected = UsernameNotFoundException.class)
    public void getOrdersForUserSadPath(){
        when(userRepository.findByUsername("username")).thenReturn(null);
        orderController.submit("username");
    }
}
