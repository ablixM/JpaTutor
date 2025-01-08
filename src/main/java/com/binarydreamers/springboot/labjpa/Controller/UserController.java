package com.binarydreamers.springboot.labjpa.Controller;
import com.binarydreamers.springboot.labjpa.DTO.LoginRequest;
import com.binarydreamers.springboot.labjpa.Model.User;
import com.binarydreamers.springboot.labjpa.Service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }


    @PostMapping("/register")
    @CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        try {
            String Email = user.getEmail();
            String username = user.getUserName();
            String password = user.getPassword();
            String roleName = user.getRoles().stream()
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("No role provided"))
                    .getName();

            userService.registerUser(Email, username, password, roleName);

            return ResponseEntity.ok("User registered successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    @CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            String email = loginRequest.getEmail();
            String password = loginRequest.getPassword();

            User user = userService.loginUser(email, password);

            return ResponseEntity.ok(user);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }



    @PutMapping("/update/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        try {
            User updatedUser = userService.updateUser(id, user);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/delete/{userId}")
    public String deleteUserById(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return "User with ID " + userId + " deleted successfully!";
    }


    @DeleteMapping("/delete/by-email")
    public String deleteUserByEmail(@RequestParam String email) {
        userService.deleteUserByEmail(email);
        return "User with email " + email + " deleted successfully!";
    }


    @GetMapping("/current-user")
    @ResponseBody
    public String currentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return "Current User: " + username;
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> getAdminPage() {
        return ResponseEntity.ok("Welcome to the admin page!");
    }
}