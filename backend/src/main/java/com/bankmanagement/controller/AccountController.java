package com.bankmanagement.controller;

import com.bankmanagement.dto.AccountRequest;
import com.bankmanagement.model.Account;
import com.bankmanagement.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/create")
    public ResponseEntity<Account> createAccount(@RequestParam String customerId,
            @RequestBody AccountRequest request) {
        return ResponseEntity.ok(accountService.createAccount(UUID.fromString(customerId), request));
    }

    @GetMapping("/{accountNo}")
    public ResponseEntity<Account> getAccount(@PathVariable String accountNo) {
        return ResponseEntity.ok(accountService.getAccount(accountNo));
    }

    @GetMapping("/balance/{accountNo}")
    public ResponseEntity<BigDecimal> getBalance(@PathVariable String accountNo) {
        return ResponseEntity.ok(accountService.getBalance(accountNo));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Account>> getCustomerAccounts(@PathVariable String customerId) {
        return ResponseEntity.ok(accountService.getCustomerAccounts(UUID.fromString(customerId)));
    }
}
