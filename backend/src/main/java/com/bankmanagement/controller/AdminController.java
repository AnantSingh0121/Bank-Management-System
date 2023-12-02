package com.bankmanagement.controller;

import com.bankmanagement.model.Customer;
import com.bankmanagement.model.KYC;
import com.bankmanagement.model.Transaction;
import com.bankmanagement.repository.KYCRepository;
import com.bankmanagement.repository.TransactionRepository;
import com.bankmanagement.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final CustomerService customerService;
    private final TransactionRepository transactionRepository;
    private final KYCRepository kycRepository;
    private final com.bankmanagement.service.EmailService emailService;

    @GetMapping("/customers")
    public ResponseEntity<List<Customer>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @PutMapping("/kyc/approve/{customerId}")
    public ResponseEntity<String> approveKYC(@PathVariable String customerId,
            @RequestParam String status) {
        UUID customerUuid = UUID.fromString(customerId);
        Customer customer = customerService.getCustomer(customerUuid);
        customer.setKycStatus(status);
        customerService.updateCustomer(customerUuid, customer);
        KYC kyc = kycRepository.findById(customerUuid).orElse(new KYC());
        kyc.setCustomerId(customerUuid);
        kyc.setStatus(status);
        kyc.setReviewedAt(java.time.Instant.now().toString());
        kyc.setReviewedBy("admin");
        kycRepository.save(kyc);
        emailService.sendKycStatusEmail(customer.getEmail(), customer.getName(), status);

        return ResponseEntity.ok("KYC status updated to: " + status);
    }

    @GetMapping("/transactions/all")
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        return ResponseEntity.ok(transactionRepository.findAll());
    }

    @GetMapping("/dashboard/stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        List<Customer> customers = customerService.getAllCustomers();
        List<Transaction> transactions = transactionRepository.findAll();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalCustomers", customers.size());
        stats.put("totalTransactions", transactions.size());
        stats.put("pendingKYC", customers.stream().filter(c -> "PENDING".equals(c.getKycStatus())).count());

        return ResponseEntity.ok(stats);
    }
}
