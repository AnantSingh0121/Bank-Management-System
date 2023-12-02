package com.bankmanagement.service;

import com.bankmanagement.model.Customer;
import com.bankmanagement.model.Loan;
import com.bankmanagement.repository.CustomerRepository;
import com.bankmanagement.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LoanService {

    private final LoanRepository loanRepository;
    private final CustomerRepository customerRepository;
    private final EmailService emailService;

    public Loan applyForLoan(UUID customerId, BigDecimal amount, String purpose) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        if (!"APPROVED".equals(customer.getKycStatus())) {
            throw new RuntimeException("KYC must be approved to apply for a loan");
        }

        Loan loan = new Loan();
        loan.setLoanId(UUID.randomUUID());
        loan.setCustomerId(customerId);
        loan.setAmount(amount);
        loan.setPurpose(purpose);
        loan.setStatus("PENDING");
        loan.setAppliedAt(Instant.now());

        Loan savedLoan = loanRepository.save(loan);
        emailService.sendLoanSubmissionEmail(
                customer.getEmail(),
                customer.getName(),
                amount.doubleValue(),
                purpose);

        return savedLoan;
    }

    public List<Loan> getCustomerLoans(UUID customerId) {
        return loanRepository.findByCustomerId(customerId);
    }

    public List<Loan> getAllLoans() {
        return loanRepository.findAll();
    }

    public Loan updateLoanStatus(UUID loanId, String status) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found"));

        loan.setStatus(status);
        loan.setProcessedAt(Instant.now());

        Loan updatedLoan = loanRepository.save(loan);
        Customer customer = customerRepository.findById(loan.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        emailService.sendLoanStatusEmail(
                customer.getEmail(),
                customer.getName(),
                loan.getAmount().doubleValue(),
                loan.getPurpose(),
                status);

        return updatedLoan;
    }
}
