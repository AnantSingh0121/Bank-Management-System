package com.bankmanagement.model;

import lombok.Data;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Table("loans")
public class Loan {
    @PrimaryKey
    private UUID loanId;
    private UUID customerId;
    private BigDecimal amount;
    private String purpose;
    private String status;
    private Instant appliedAt;
    private Instant processedAt;
}
