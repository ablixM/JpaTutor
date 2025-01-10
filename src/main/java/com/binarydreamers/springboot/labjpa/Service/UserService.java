package com.binarydreamers.springboot.labjpa.Service;

import com.binarydreamers.springboot.labjpa.DTO.LoginResponse;
import com.binarydreamers.springboot.labjpa.Model.Role;
import com.binarydreamers.springboot.labjpa.Model.UserNotFoundException;
import com.binarydreamers.springboot.labjpa.Model.User;
import com.binarydreamers.springboot.labjpa.Repository.RoleRepository;
import com.binarydreamers.springboot.labjpa.Repository.UserRepository;
import com.binarydreamers.springboot.labjpa.Util.JwtUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class    UserService implements UserDetailsService {
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private final UserRepository userRepository;
    private final RoleRepository roleRepository ;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User registerUser(String email, String name, String password, String roleName) {

        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("User already exists with email: " + email);
        }

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleName));

        User user = new User();
        user.setEmail(email);
        user.setUserName(name);
        user.setPassword(passwordEncoder.encode(password)); // Encode the password
        user.getRoles().clear();
        user.getRoles().add(role);

        return userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.getAuthorities()
        );
    }

    public LoginResponse loginUser(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));

        if (passwordEncoder.matches(password, user.getPassword())) {
            // Generate JWT token
            UserDetails userDetails = loadUserByUsername(email);
            String token = jwtUtil.generateToken(userDetails);

            return new LoginResponse(user, token); // Return custom response object
        } else {
            throw new IllegalArgumentException("Invalid password");
        }
    }


    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        userRepository.delete(user);
    }

    @Transactional
    public void deleteUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));

        userRepository.delete(user);
    }

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }
    public User getUserById(Long id){
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
    }

    public User updateUser(Long id, User user) {
        var oldUser = getUserById(id);
        if (oldUser == null) {
            throw new UserNotFoundException("User not found with id: " + id);
        }
        oldUser.setUserName(user.getUserName());
        oldUser.setEmail(user.getEmail());
        userRepository.save(oldUser);
        return oldUser;
    }



}