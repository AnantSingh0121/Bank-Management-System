package com.bankmanagement.repository;

import com.bankmanagement.model.Loan;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LoanRepository extends CassandraRepository<Loan, UUID> {
    @AllowFiltering
    List<Loan> findByCustomerId(UUID customerId);

    @AllowFiltering
    List<Loan> findByStatus(String status);
}
