package com.bankmanagement.repository;

import com.bankmanagement.model.KYC;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface KYCRepository extends CassandraRepository<KYC, UUID> {
}
