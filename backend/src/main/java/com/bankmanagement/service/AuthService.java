package com.bankmanagement.service;

import com.bankmanagement.dto.AuthResponse;
import com.bankmanagement.dto.LoginRequest;
import com.bankmanagement.dto.RegisterRequest;
import com.bankmanagement.model.Customer;
import com.bankmanagement.model.User;
import com.bankmanagement.repository.CustomerRepository;
import com.bankmanagement.repository.UserRepository;
import com.bankmanagement.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final EmailService emailService;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        List<Customer> existingCustomers = customerRepository.findAll();
        if (existingCustomers.stream().anyMatch(c -> c.getEmail().equalsIgnoreCase(request.getEmail()))) {
            throw new RuntimeException("Email already registered");
        }
        if (existingCustomers.stream().anyMatch(c -> c.getPhone().equals(request.getPhone()))) {
            throw new RuntimeException("Phone number already registered");
        }
        UUID customerId = UUID.randomUUID();
        Customer customer = new Customer();
        customer.setCustomerId(customerId);
        customer.setName(request.getName());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());
        customer.setAddress(request.getAddress());
        customer.setKycStatus("PENDING");
        customer.setCreatedAt(Instant.now().toString());
        customerRepository.save(customer);
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("CUSTOMER");
        user.setCustomerId(customerId.toString());
        userRepository.save(user);
        String token = jwtTokenProvider.generateToken(user.getUsername(), user.getRole());
        emailService.sendKycSubmissionEmail(customer.getEmail(), customer.getName());

        return new AuthResponse(token, user.getUsername(), user.getRole(), customerId.toString());
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtTokenProvider.generateToken(user.getUsername(), user.getRole());

        return new AuthResponse(token, user.getUsername(), user.getRole(), user.getCustomerId());
    }
}
