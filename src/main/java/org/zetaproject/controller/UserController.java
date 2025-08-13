package org.zetaproject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.zetaproject.dto.UserResponse;
import org.zetaproject.dto.RegisterRequest;
import org.zetaproject.model.entites.User;
import org.zetaproject.services.UserService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public UserResponse createUser(@RequestBody RegisterRequest request) {
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword()); // In a real app, hash this password!
        user.setRole(request.getRole());
        User createdUser = userService.createUser(user);
        return new UserResponse(createdUser.getId(), createdUser.getName(), createdUser.getEmail(), createdUser.getRole());
    }

    @GetMapping
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers().stream()
                .map(user -> new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole()))
                .collect(Collectors.toList());
    }
}