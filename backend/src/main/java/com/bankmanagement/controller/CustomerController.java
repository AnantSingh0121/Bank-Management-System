package com.bankmanagement.controller;

import com.bankmanagement.model.Customer;
import com.bankmanagement.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping("/profile")
    public ResponseEntity<Customer> getProfile(@RequestParam String customerId) {
        return ResponseEntity.ok(customerService.getCustomer(UUID.fromString(customerId)));
    }

    @PutMapping("/profile")
    public ResponseEntity<Customer> updateProfile(@RequestParam String customerId,
            @RequestBody Customer customer) {
        return ResponseEntity.ok(customerService.updateCustomer(UUID.fromString(customerId), customer));
    }
}
