package com.banking.appbank.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banking.appbank.entity.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction,String>{
    
}
