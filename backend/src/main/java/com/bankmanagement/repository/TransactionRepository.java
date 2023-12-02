package com.bankmanagement.repository;

import com.bankmanagement.model.Transaction;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface TransactionRepository extends CassandraRepository<Transaction, String> {
    @Query("SELECT * FROM transactions WHERE account_no = ?0 LIMIT ?1")
    List<Transaction> findByAccountNoLimit(String accountNo, int limit);
}
