package com.bankmanagement.controller;

import com.bankmanagement.dto.TransactionRequest;
import com.bankmanagement.model.Transaction;
import com.bankmanagement.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transaction")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/deposit")
    public ResponseEntity<Transaction> deposit(@RequestBody TransactionRequest request) {
        return ResponseEntity.ok(transactionService.deposit(request));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<Transaction> withdraw(@RequestBody TransactionRequest request) {
        return ResponseEntity.ok(transactionService.withdraw(request));
    }

    @PostMapping("/transfer")
    public ResponseEntity<String> transfer(@RequestBody TransactionRequest request) {
        transactionService.transfer(request);
        return ResponseEntity.ok("Transfer successful");
    }

    @GetMapping("/history/{accountNo}")
    public ResponseEntity<List<Transaction>> getHistory(@PathVariable String accountNo,
            @RequestParam(defaultValue = "50") int limit) {
        return ResponseEntity.ok(transactionService.getTransactionHistory(accountNo, limit));
    }
}
