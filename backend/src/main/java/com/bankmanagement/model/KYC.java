package com.bankmanagement.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("kyc")
public class KYC {
    @PrimaryKey
    private UUID customerId;
    private String idProofPath;
    private String addressProofPath;
    private String status; 
    private String submittedAt;
    private String reviewedAt;
    private String reviewedBy;
}
