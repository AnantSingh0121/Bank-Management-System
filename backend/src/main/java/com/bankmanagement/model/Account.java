package com.bankmanagement.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("accounts")
public class Account {
    @PrimaryKey
    private String accountNo;
    private UUID customerId;
    private String accountType;
    private BigDecimal balance;
    private String status; 
    private String createdAt;
}
