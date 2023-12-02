package com.bankmanagement.service;

import com.bankmanagement.dto.AccountRequest;
import com.bankmanagement.model.Account;
import com.bankmanagement.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    public Account createAccount(UUID customerId, AccountRequest request) {
        Account account = new Account();
        account.setAccountNo(generateAccountNumber());
        account.setCustomerId(customerId);
        account.setAccountType(request.getAccountType());
        account.setBalance(BigDecimal.ZERO);
        account.setStatus("ACTIVE");
        account.setCreatedAt(Instant.now().toString());

        return accountRepository.save(account);
    }

    public Account getAccount(String accountNo) {
        return accountRepository.findById(accountNo)
                .orElseThrow(() -> new RuntimeException("Account not found"));
    }

    public List<Account> getCustomerAccounts(UUID customerId) {
        return accountRepository.findByCustomerId(customerId);
    }

    public BigDecimal getBalance(String accountNo) {
        Account account = getAccount(accountNo);
        return account.getBalance();
    }

    private String generateAccountNumber() {
        Random random = new Random();
        long number = 1000000000L + (long) (random.nextDouble() * 9000000000L);
        return String.valueOf(number);
    }
}
