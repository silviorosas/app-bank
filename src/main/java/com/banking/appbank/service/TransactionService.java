package com.banking.appbank.service;

import com.banking.appbank.dto.TransactionDto;


public interface TransactionService {
    void saveTransaction(TransactionDto transactionDto);
}
