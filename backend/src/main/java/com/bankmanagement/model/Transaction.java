package com.bankmanagement.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("transactions")
public class Transaction {
    @PrimaryKeyColumn(name = "account_no" ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private String accountNo;
    
    @PrimaryKeyColumn(name = "txn_timestamp" ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    private Instant txnTimestamp;
    
    private UUID txnId;
    private String txnType;
    private BigDecimal amount;
    private BigDecimal balanceAfter;
    private String toAccount;
    private String description;
}
