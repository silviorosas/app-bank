package com.banking.appbank.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.banking.appbank.dto.TransactionDto;
import com.banking.appbank.entity.Transaction;
import com.banking.appbank.repository.TransactionRepository;

@Service
public class TransactionServiceImpl implements TransactionService  {

    @Autowired
    TransactionRepository transactionRepository;

    @Override
    public void saveTransaction(TransactionDto transactionDto) {
        Transaction transaction = Transaction.builder()
                .transactionType(transactionDto.getTransactionType())
                .accountNumber(transactionDto.getAccountNumber())
                .amount(transactionDto.getAmount())
                .status("SUCCESS")
                .build();     

        transactionRepository.save(transaction);
        System.out.println("Transacción exitosa");        
    }
    
}
