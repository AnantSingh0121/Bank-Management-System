package com.bankmanagement.controller;

import com.bankmanagement.model.Loan;
import com.bankmanagement.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/loan")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;

    @PostMapping("/apply")
    public ResponseEntity<Loan> applyForLoan(@RequestBody Map<String, Object> request) {
        UUID customerId = UUID.fromString((String) request.get("customerId"));
        BigDecimal amount = new BigDecimal(String.valueOf(request.get("amount")));
        String purpose = (String) request.get("purpose");

        return ResponseEntity.ok(loanService.applyForLoan(customerId, amount, purpose));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Loan>> getCustomerLoans(@PathVariable String customerId) {
        return ResponseEntity.ok(loanService.getCustomerLoans(UUID.fromString(customerId)));
    }

    @GetMapping("/all")
    public ResponseEntity<List<Loan>> getAllLoans() {
        return ResponseEntity.ok(loanService.getAllLoans());
    }

    @PutMapping("/{loanId}/status")
    public ResponseEntity<Loan> updateStatus(@PathVariable String loanId, @RequestParam String status) {
        return ResponseEntity.ok(loanService.updateLoanStatus(UUID.fromString(loanId), status));
    }
}
