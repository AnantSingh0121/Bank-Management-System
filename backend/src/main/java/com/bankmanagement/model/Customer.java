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
@Table("customers")
public class Customer {
    @PrimaryKey
    private UUID customerId;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String kycStatus; 
    private String createdAt;
}
