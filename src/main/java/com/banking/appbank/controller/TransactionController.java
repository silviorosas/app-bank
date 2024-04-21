package com.banking.appbank.controller;

import java.io.FileNotFoundException;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.banking.appbank.entity.Transaction;
import com.banking.appbank.service.BankStatement;
import com.itextpdf.text.DocumentException;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/bankStatement")
@AllArgsConstructor
public class TransactionController {
    
    private BankStatement bankStatement;

    @GetMapping
    public List<Transaction> generateBankStatement(@RequestParam String accountNumber, 
                                                   @RequestParam String startDate, 
                                                   @RequestParam String endDate) throws FileNotFoundException, DocumentException{
       return bankStatement.generateStatement(accountNumber, startDate, endDate);

    }
}
