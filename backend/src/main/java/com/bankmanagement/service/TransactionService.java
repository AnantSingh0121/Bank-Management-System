package com.bankmanagement.service;

import com.bankmanagement.dto.TransactionRequest;
import com.bankmanagement.model.Account;
import com.bankmanagement.model.Transaction;
import com.bankmanagement.repository.AccountRepository;
import com.bankmanagement.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final com.bankmanagement.repository.CustomerRepository customerRepository;

    private void validateKyc(UUID customerId) {
        com.bankmanagement.model.Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        if (!"APPROVED".equals(customer.getKycStatus())) {
            throw new RuntimeException("Transaction blocked: KYC not approved");
        }
    }

    @Transactional
    public Transaction deposit(TransactionRequest request) {
        Account account = accountRepository.findById(request.getAccountNo())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        validateKyc(account.getCustomerId());

        if (!"ACTIVE".equals(account.getStatus())) {
            throw new RuntimeException("Account is not active");
        }
        BigDecimal newBalance = account.getBalance().add(request.getAmount());
        account.setBalance(newBalance);
        accountRepository.save(account);
        Transaction transaction = new Transaction();
        transaction.setAccountNo(request.getAccountNo());
        transaction.setTxnTimestamp(Instant.now());
        transaction.setTxnId(UUID.randomUUID());
        transaction.setTxnType("DEPOSIT");
        transaction.setAmount(request.getAmount());
        transaction.setBalanceAfter(newBalance);
        transaction.setDescription(request.getDescription());

        return transactionRepository.save(transaction);
    }

    @Transactional
    public Transaction withdraw(TransactionRequest request) {
        Account account = accountRepository.findById(request.getAccountNo())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        validateKyc(account.getCustomerId());

        if (!"ACTIVE".equals(account.getStatus())) {
            throw new RuntimeException("Account is not active");
        }

        if (account.getBalance().compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("Insufficient balance");
        }
        BigDecimal newBalance = account.getBalance().subtract(request.getAmount());
        account.setBalance(newBalance);
        accountRepository.save(account);
        Transaction transaction = new Transaction();
        transaction.setAccountNo(request.getAccountNo());
        transaction.setTxnTimestamp(Instant.now());
        transaction.setTxnId(UUID.randomUUID());
        transaction.setTxnType("WITHDRAW");
        transaction.setAmount(request.getAmount());
        transaction.setBalanceAfter(newBalance);
        transaction.setDescription(request.getDescription());

        return transactionRepository.save(transaction);
    }

    @Transactional
    public void transfer(TransactionRequest request) {
        Account fromAccount = accountRepository.findById(request.getAccountNo())
                .orElseThrow(() -> new RuntimeException("Source account not found"));
        Account toAccount = accountRepository.findById(request.getToAccount())
                .orElseThrow(() -> new RuntimeException("Destination account not found"));

        validateKyc(fromAccount.getCustomerId());

        if (!"ACTIVE".equals(fromAccount.getStatus()) || !"ACTIVE".equals(toAccount.getStatus())) {
            throw new RuntimeException("One or both accounts are not active");
        }

        if (fromAccount.getBalance().compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        Instant now = Instant.now();
        BigDecimal fromNewBalance = fromAccount.getBalance().subtract(request.getAmount());
        fromAccount.setBalance(fromNewBalance);
        accountRepository.save(fromAccount);

        Transaction debitTxn = new Transaction();
        debitTxn.setAccountNo(request.getAccountNo());
        debitTxn.setTxnTimestamp(now);
        debitTxn.setTxnId(UUID.randomUUID());
        debitTxn.setTxnType("TRANSFER_OUT");
        debitTxn.setAmount(request.getAmount());
        debitTxn.setBalanceAfter(fromNewBalance);
        debitTxn.setToAccount(request.getToAccount());
        debitTxn.setDescription(request.getDescription());
        transactionRepository.save(debitTxn);
        BigDecimal toNewBalance = toAccount.getBalance().add(request.getAmount());
        toAccount.setBalance(toNewBalance);
        accountRepository.save(toAccount);

        Transaction creditTxn = new Transaction();
        creditTxn.setAccountNo(request.getToAccount());
        creditTxn.setTxnTimestamp(now);
        creditTxn.setTxnId(UUID.randomUUID());
        creditTxn.setTxnType("TRANSFER_IN");
        creditTxn.setAmount(request.getAmount());
        creditTxn.setBalanceAfter(toNewBalance);
        creditTxn.setToAccount(request.getAccountNo());
        creditTxn.setDescription(request.getDescription());
        transactionRepository.save(creditTxn);
    }

    public List<Transaction> getTransactionHistory(String accountNo, int limit) {
        return transactionRepository.findByAccountNoLimit(accountNo, limit);
    }
}
