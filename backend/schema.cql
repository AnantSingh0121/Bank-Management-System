-- Cassandra Schema for Bank Management System

-- Create Keyspace
CREATE KEYSPACE IF NOT EXISTS bankmgmt 
WITH replication = {'class':'SimpleStrategy', 'replication_factor':1};

USE bankmgmt;

-- Users Table
CREATE TABLE IF NOT EXISTS users (
    username text PRIMARY KEY,
    password text,
    role text,
    customer_id text
);

-- Customers Table
CREATE TABLE IF NOT EXISTS customers (
    customer_id uuid PRIMARY KEY,
    name text,
    email text,
    phone text,
    address text,
    kyc_status text,
    created_at text
);

-- Accounts Table
CREATE TABLE IF NOT EXISTS accounts (
    account_no text PRIMARY KEY,
    customer_id uuid,
    account_type text,
    balance decimal,
    status text,
    created_at text
);

-- Transactions Table (Optimized for time-series queries)
CREATE TABLE IF NOT EXISTS transactions (
    account_no text,
    txn_timestamp timestamp,
    txn_id uuid,
    txn_type text,
    amount decimal,
    balance_after decimal,
    to_account text,
    description text,
    PRIMARY KEY (account_no, txn_timestamp)
) WITH CLUSTERING ORDER BY (txn_timestamp DESC);

-- KYC Table
CREATE TABLE IF NOT EXISTS kyc (
    customer_id uuid PRIMARY KEY,
    id_proof_path text,
    address_proof_path text,
    status text,
    submitted_at text,
    reviewed_at text,
    reviewed_by text
);

-- Create admin user (password: admin123)
INSERT INTO users (username, password, role, customer_id) 
VALUES ('admin', '$2b$12$cy91Nm2yDc.hHDGEPlFCQOFXy8CVuuTunnhFH1OBD74oVgq5SsU8S', 'ADMIN', null);
