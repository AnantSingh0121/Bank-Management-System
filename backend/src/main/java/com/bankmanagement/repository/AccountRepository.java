package com.bankmanagement.repository;

import com.bankmanagement.model.Account;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AccountRepository extends CassandraRepository<Account, String> {
    @Query("SELECT * FROM accounts WHERE customer_id = ?0 ALLOW FILTERING")
    List<Account> findByCustomerId(UUID customerId);
}
